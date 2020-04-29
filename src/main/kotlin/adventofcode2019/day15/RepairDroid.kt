package adventofcode2019.day15

import adventofcode2019.day15.Command.EXIT
import adventofcode2019.day15.Command.MovementCommand
import adventofcode2019.day15.Direction.EAST
import adventofcode2019.day15.Direction.NORTH
import adventofcode2019.day15.Direction.SOUTH
import adventofcode2019.day15.Direction.WEST
import adventofcode2019.day15.GridCell.DRONE
import adventofcode2019.day15.GridCell.DRONE_OXYGEN_SYSTEM
import adventofcode2019.day15.GridCell.EXPLORED
import adventofcode2019.day15.GridCell.FOG
import adventofcode2019.day15.GridCell.OXYGEN_SYSTEM
import adventofcode2019.day15.GridCell.WALL
import adventofcode2019.day15.Status.HIT_THE_WALL
import adventofcode2019.day15.Status.MOVE
import adventofcode2019.day15.Status.MOVE_AND_FOUND
import adventofcode2019.grid.ArrayGrid
import adventofcode2019.grid.Bounds
import adventofcode2019.grid.Grid
import adventofcode2019.grid.Position
import adventofcode2019.intcode.ChannelMonoBus
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.ReceiveBus
import adventofcode2019.intcode.SendBus
import adventofcode2019.intcode.TransformingInputProvider
import adventofcode2019.intcode.TransformingOutputConsumer
import adventofcode2019.intcode.createIntCodeAllInstr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Deque
import java.util.LinkedList

internal class RepairDroid private constructor(
    private val droidProgram: IntCode,
    private val droidProgramInput: SendBus<Direction>,
    private val droidProgramOutput: ReceiveBus<Status>
) {
    companion object {
        fun fromMemory(memory: Memory): RepairDroid {
            val transformingInputProvider = TransformingInputProvider(
                ChannelMonoBus(Channel()),
                Direction::toIntCodeNumber
            )
            val transformingOutputConsumer = TransformingOutputConsumer(
                ChannelMonoBus(Channel()),
                IntCodeNumber::toStatus
            )
            return RepairDroid(
                createIntCodeAllInstr(
                    memory,
                    InputInstruction(transformingInputProvider),
                    OutputInstruction(transformingOutputConsumer)
                ),
                transformingInputProvider,
                transformingOutputConsumer
            )
        }

        private fun command(grid: ArrayGrid<GridCell>, dronePosition: Position, moves: Deque<Direction>): Command {
            fun move(): Direction? {
                return Direction.values().firstOrNull() { direction ->
                    grid[dronePosition + direction] == FOG
                } ?: moves.peek()?.opposite()
            }
            return move()?.toMovementCommand() ?: EXIT
        }

        private fun readCommand(): Command {
            var command: Command? = null
            do {
                val str = readLine() ?: error("Redirected")
                command = when (str) {
                    "EXIT" -> EXIT
                    else -> try {
                        MovementCommand(str.toDirection())
                    } catch (e: IllegalStateException) {
                        null
                    }
                }
            } while (command == null)
            return command
        }

        private fun drawGrid(grid: ArrayGrid<GridCell>) {
            val lineSeparator = System.lineSeparator()
            val boundChar = "."
            val horizontalBound = boundChar.repeat(grid.bounds.size().width + 2)
            println(
                horizontalBound
                    + lineSeparator
                    + grid.toString(GridCell::asString).lines()
                    .joinToString(lineSeparator) { line -> boundChar + line + boundChar }
                    + lineSeparator
                    + horizontalBound
                    + lineSeparator
            )
        }
    }

    fun buildMap() = runBlocking {
        val droidProgramJob = launch { droidProgram.run() }
        val map = withContext(Dispatchers.Default) { buildMapAutomatically() }
        droidProgramJob.cancelAndJoin()
        map
    }

    private suspend fun buildMapManually(): Pair<Grid<GridCell>, Int> {
        return buildMap { _, _, _ -> readCommand() }
    }

    private suspend fun buildMapAutomatically(): Pair<Grid<GridCell>, Int> {
        return buildMap(Companion::command)
    }

    private suspend fun buildMap(
        commandResolver: (grid: ArrayGrid<GridCell>, dronePosition: Position, moves: Deque<Direction>) -> Command
    ): Pair<Grid<GridCell>, Int> {
        var dronePosition = Position.ZERO
        val grid: ArrayGrid<GridCell> = ArrayGrid.withBounds(Bounds(-21, 20, -19, 21)) { pos ->
            when (pos) {
                dronePosition -> DRONE
                else -> FOG
            }
        }
        val moves: Deque<Direction> = LinkedList()
        var exit = false
        var movesToOxygenSystem = 0
        while (!exit) {
            when (val command = commandResolver(grid, dronePosition, moves)) {
                is MovementCommand -> {
                    val direction = command.direction
                    droidProgramInput.send(direction)
                    val status = droidProgramOutput.receive()
                    val pos = dronePosition + direction
                    when (status) {
                        HIT_THE_WALL -> grid.changeElements(sequenceOf(pos to WALL))
                        in setOf(MOVE, MOVE_AND_FOUND) -> {
                            if (direction == moves.peek()?.opposite()) {
                                moves.pop()
                            } else {
                                moves.push(direction)
                            }
                            if (status == MOVE_AND_FOUND && movesToOxygenSystem == 0) {
                                movesToOxygenSystem = moves.size
                            }
                            grid.changeElements(
                                sequenceOf(
                                    pos to if (status == MOVE) DRONE else DRONE_OXYGEN_SYSTEM,
                                    dronePosition to when {
                                        status == MOVE && grid[dronePosition] == DRONE_OXYGEN_SYSTEM -> OXYGEN_SYSTEM
                                        else -> EXPLORED
                                    }
                                )
                            )
                            dronePosition = pos
                        }
                    }
                }
                is EXIT -> exit = true
            }
        }
        return grid to movesToOxygenSystem
    }
}

private sealed class Command {
    object EXIT : Command()
    data class MovementCommand(val direction: Direction) : Command()
}

private fun Direction.toMovementCommand(): MovementCommand = MovementCommand(this)

internal enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

private fun String.toDirection(): Direction = when (this) {
    "N" -> NORTH
    "S" -> SOUTH
    "W" -> WEST
    "E" -> EAST
    else -> error("Unknown direction: $this")
}

private fun Direction.toIntCodeNumber(): IntCodeNumber = when (this) {
    NORTH -> IntCodeNumber.ONE
    SOUTH -> IntCodeNumber.TWO
    WEST -> IntCodeNumber.fromInt(3)
    EAST -> IntCodeNumber.fromInt(4)
}

private fun Direction.opposite(): Direction = when (this) {
    NORTH -> SOUTH
    SOUTH -> NORTH
    WEST -> EAST
    EAST -> WEST
}

internal operator fun Position.plus(direction: Direction): Position = when (direction) {
    NORTH -> Position(x, y + 1)
    SOUTH -> Position(x, y - 1)
    WEST -> Position(x - 1, y)
    EAST -> Position(x + 1, y)
}

private enum class Status {
    HIT_THE_WALL, MOVE, MOVE_AND_FOUND
}

private fun IntCodeNumber.toStatus(): Status = when (this) {
    IntCodeNumber.ZERO -> HIT_THE_WALL
    IntCodeNumber.ONE -> MOVE
    IntCodeNumber.TWO -> MOVE_AND_FOUND
    else -> error("Unknown status code: $this")
}

internal enum class GridCell {
    FOG, DRONE, DRONE_OXYGEN_SYSTEM, WALL, EXPLORED, OXYGEN_SYSTEM
}

private fun GridCell.asString(): String = when (this) {
    FOG -> " "
    DRONE -> "0"
    DRONE_OXYGEN_SYSTEM -> "@"
    WALL -> "#"
    EXPLORED -> "+"
    OXYGEN_SYSTEM -> "X"
}
