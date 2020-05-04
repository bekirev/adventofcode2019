package adventofcode2019.day13

import adventofcode2019.day13.Tile.BALL
import adventofcode2019.day13.Tile.BLOCK
import adventofcode2019.day13.Tile.EMPTY
import adventofcode2019.day13.Tile.HORIZONTAL_PADDLE
import adventofcode2019.day13.Tile.WALL
import adventofcode2019.grid.ArrayGrid
import adventofcode2019.grid.Bounds
import adventofcode2019.grid.Position
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ChannelInputProvider
import adventofcode2019.intcode.ChannelOutputConsumer
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.LinkedList

internal class GameOnGrid private constructor(
    private val gameProgram: IntCode,
    private val gameInputChannel: Channel<IntCodeNumber>,
    private val gameOutputChannel: Channel<IntCodeNumber>,
    firstSteps: List<Int>
) {
    private val updateViewChannel = Channel<GameData>()
    private val provideInputChannel = Channel<Unit>()
    private val updateModelChannel = Channel<Unit>()
    private var score: Int = 0
    private var gameGrid: ArrayGrid<Tile> = ArrayGrid.withBounds(Bounds(0, 43, 0, 19)) { Tile.EMPTY }
    private var screenGrid: ArrayGrid<ScreenCell> = ArrayGrid.withBounds(gameGrid.bounds) { ScreenCell.EMPTY }
    private var ballPrevPos: Position? = null
    private val steps = mutableListOf<Int>()
    private val stepsToDo = LinkedList(firstSteps)

    companion object {
        private const val MAX_TRAJECTORY_LENGTH = 50

        fun fromMemory(memory: Memory, firstSteps: List<Int> = emptyList()): GameOnGrid {
            val gameInputChannel = Channel<IntCodeNumber>(Channel.UNLIMITED)
            val gameOutputChannel = Channel<IntCodeNumber>(Channel.UNLIMITED)
            return GameOnGrid(
                createIntCodeAllInstr(
                    AdditionalMapMemory.fromMemory(memory),
                    InputInstruction(ChannelInputProvider(gameInputChannel)),
                    OutputInstruction(ChannelOutputConsumer(gameOutputChannel))
                ),
                gameInputChannel,
                gameOutputChannel,
                firstSteps
            )
        }
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun run() = runBlocking {
        val gameProgramJob = launch { gameProgram.run() }
        val getGameModelJob = launch { getGameModel() }
        val drawGridJob = launch { drawGrid() }
        val provideInputJob = launch { provideInput() }
        gameProgramJob.join()
        delay(100)
        getGameModelJob.cancelAndJoin()
        drawGridJob.cancelAndJoin()
        provideInputJob.cancelAndJoin()
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private suspend fun getGameModel() = coroutineScope {
        while (isActive) {
            val tiles = mutableMapOf<Position, Tile>()
            var score: Int? = null
            while (!gameOutputChannel.isEmpty) {
                val x = gameOutputChannel.receive()
                val y = gameOutputChannel.receive()
                val thirdParam = gameOutputChannel.receive()
                if (x == IntCodeNumber.of(-1) && y == IntCodeNumber.ZERO) {
                    score = thirdParam.toInt()
                    this@GameOnGrid.score = score
                } else {
                    tiles[Position(x.toInt(), y.toInt())] = thirdParam.toTile()
                }
            }
            updateViewChannel.send(GameData(tiles, score))
            updateModelChannel.receive()
        }
    }

    private suspend fun drawGrid() = coroutineScope {
        fun ScreenCell.asString() = when (this) {
            ScreenCell.EMPTY -> " "
            ScreenCell.WALL -> "#"
            ScreenCell.BLOCK -> "_"
            ScreenCell.HORIZONTAL_PADDLE -> "="
            ScreenCell.BALL -> "0"
            ScreenCell.TRAJECTORY -> "*"
        }

        while (isActive) {
            val gameData = updateViewChannel.receive()
            gameGrid.changeElements(
                gameData.grid.asSequence().map(Map.Entry<Position, Tile>::toPair)
            )
            val ballPos = gameGrid.bounds.allPositions().first { pos ->
                gameGrid[pos] == BALL
            }
            val ballPrevPosition = ballPrevPos
            val trajectory = when (ballPrevPosition) {
                null -> {
                    ballPrevPos = ballPos
                    emptyList()
                }
                else -> {
                    val trajectory = trajectory(ballPrevPosition, ballPos)
                    ballPrevPos = ballPos
                    trajectory
                }
            }.toSet()
            screenGrid.changeElements(
                gameGrid.bounds.allPositions().map { pos ->
                    pos to when {
                        gameGrid[pos] == BALL -> ScreenCell.BALL
                        pos in trajectory -> ScreenCell.TRAJECTORY
                        else -> gameGrid[pos].toScreenCell()
                    }
                }
            )
            if (gameData.score != null) {
                score = gameData.score
            }
            System.lineSeparator().also { lineSeparator ->
                println(
                    "-".repeat(screenGrid.bounds.size().width)
                        + lineSeparator
                        + "Score: $score"
                        + lineSeparator
                        + screenGrid.toString(ScreenCell::asString).lines().reversed().joinToString(lineSeparator)
                        + lineSeparator
                        + steps.joinToString(",")
                )
            }
            provideInputChannel.send(Unit)
        }
    }


    private fun trajectory(prevPos: Position, pos: Position): List<Position> {
        tailrec fun trajectory(list: List<Position>, prevPos: Position, pos: Position): List<Position> {
            fun isTransparent(pos: Position): Boolean = when (gameGrid[pos]) {
                EMPTY -> true
                else -> false
            }
            return when {
                list.size < MAX_TRAJECTORY_LENGTH -> {
                    val angle = pos - prevPos
                    when {
                        pos.y + angle.y <= gameGrid.bounds.maxY -> {
                            val nextPos = when (gameGrid[pos + angle]) {
                                BLOCK, HORIZONTAL_PADDLE -> pos - angle
                                else -> Position(
                                    pos.x + if (isTransparent(pos.plusX(angle.x))) angle.x else -angle.x,
                                    pos.y + if (isTransparent(pos.plusY(angle.y))) angle.y else -angle.y
                                )
                            }
                            trajectory(list.plus(nextPos), pos, nextPos)
                        }
                        else -> list
                    }
                }
                else -> list
            }
        }
        return trajectory(emptyList(), prevPos, pos)
    }

    private suspend fun provideInput() = coroutineScope {
        while (isActive) {
            provideInputChannel.receive()
            val readValue = when (val stepToDo: Int? = stepsToDo.poll()) {
                null -> {
                    val readValueFn = { readLine()?.toIntOrNull() }
                    tailrec fun readFromStdInputStream(readValue: Int?): Int = when (readValue) {
                        -1, 0, 1 -> readValue
                        else -> readFromStdInputStream(readValueFn())
                    }
                    readFromStdInputStream(readValueFn())
                }
                else -> stepToDo
            }
            steps.add(readValue)
            gameInputChannel.send(IntCodeNumber.of(readValue))
            updateModelChannel.send(Unit)
        }
    }
}

private data class GameData(
    val grid: Map<Position, Tile>,
    val score: Int?
)

private enum class Tile {
    EMPTY, WALL, BLOCK, HORIZONTAL_PADDLE, BALL
}

private enum class ScreenCell {
    EMPTY, WALL, BLOCK, HORIZONTAL_PADDLE, BALL, TRAJECTORY
}

private fun IntCodeNumber.toTile(): Tile = when (this) {
    IntCodeNumber.ZERO -> EMPTY
    IntCodeNumber.ONE -> WALL
    IntCodeNumber.TWO -> BLOCK
    IntCodeNumber.of(3) -> HORIZONTAL_PADDLE
    IntCodeNumber.of(4) -> BALL
    else -> throw IllegalArgumentException("Unknown tile code: $this")
}

private fun Tile.toScreenCell(): ScreenCell = when (this) {
    EMPTY -> ScreenCell.EMPTY
    WALL -> ScreenCell.WALL
    BLOCK -> ScreenCell.BLOCK
    HORIZONTAL_PADDLE -> ScreenCell.HORIZONTAL_PADDLE
    BALL -> ScreenCell.BALL
}
