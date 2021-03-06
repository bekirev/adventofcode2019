package adventofcode2019.day19

import adventofcode2019.day19.SpaceState.BEING_PULLED
import adventofcode2019.day19.SpaceState.STATIONARY
import adventofcode2019.grid.Bounds
import adventofcode2019.grid.Position
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.ReceiveBus
import adventofcode2019.intcode.SendBus
import adventofcode2019.intcode.TransformingInputProvider
import adventofcode2019.intcode.TransformingOutputConsumer
import adventofcode2019.intcode.asMonoBus
import adventofcode2019.intcode.createIntCodeAllInstr
import adventofcode2019.intcode.intCodeInput
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main() {
    val spaceScanner = CachingSpaceScanner(
        Drones(
            ArrayMemory.of(
                Paths.get("adventofcode2019", "day19", "input.txt")
                    .intCodeInput()
            )
        )
    )
    println(
        Bounds(0, 49, 0, 49)
            .allPositions()
            .map(spaceScanner::scan)
            .count(BEING_PULLED::equals)
    )
    val minValue = 100
    val minPos = generateSequence(10, 1::plus)
        .map(spaceScanner::maxCubeSizeInRow)
        .first { it.second >= minValue }
        .first
    println(minPos.x * 10000 + minPos.y)
}

private interface SpaceScanner {
    fun scan(position: Position): SpaceState
}

private class CachingSpaceScanner(private val spaceScanner: SpaceScanner) : SpaceScanner {
    private val mapCache: HashMap<Position, SpaceState> = HashMap()

    override fun scan(position: Position): SpaceState = mapCache.getOrPut(position) { spaceScanner.scan(position) }
}

private class Drones(private val memory: ArrayMemory) : SpaceScanner {
    companion object {
        private class Drone private constructor(
            private val program: IntCode,
            private val programInput: SendBus<Int>,
            private val programOutput: ReceiveBus<SpaceState>
        ) {
            companion object {
                fun fromMemory(memory: Memory): Drone {
                    val transformingInputProvider = TransformingInputProvider<Int>(
                        Channel<IntCodeNumber>().asMonoBus()
                    ) { IntCodeNumber.of(it) }
                    val transformingOutputConsumer = TransformingOutputConsumer(
                        Channel<IntCodeNumber>().asMonoBus(),
                        IntCodeNumber::toCellState
                    )
                    return Drone(
                        createIntCodeAllInstr(
                            AdditionalMapMemory.fromMemory(memory),
                            InputInstruction(transformingInputProvider),
                            OutputInstruction(transformingOutputConsumer)
                        ),
                        transformingInputProvider,
                        transformingOutputConsumer
                    )
                }
            }

            fun scan(position: Position): SpaceState = runBlocking {
                val programJob = launch { program.run() }
                programInput.send(position.x)
                programInput.send(position.y)
                val result = programOutput.receive()
                programJob.join()
                result
            }
        }
    }

    override fun scan(position: Position): SpaceState {
        return Drone.fromMemory(AdditionalMapMemory.fromMemory(memory.copyOf())).scan(position)
    }
}

private enum class SpaceState {
    STATIONARY, BEING_PULLED
}

private fun IntCodeNumber.toCellState(): SpaceState = when (this) {
    IntCodeNumber.ZERO -> STATIONARY
    IntCodeNumber.ONE -> BEING_PULLED
    else -> error("Unknown cell state code: $this")
}

private fun SpaceScanner.maxCubeSizeInRow(row: Int): Pair<Position, Int> {
    fun maxCubeSizeFromPosition(position: Position): Int {
        tailrec fun maxCubeSizeFromPosition(edgeSize: Int): Int {
            return if (
                sequenceOf(
                    scan(position.plusX(edgeSize)),
                    scan(position.plusY(edgeSize))
                ).all(BEING_PULLED::equals)
            ) {
                maxCubeSizeFromPosition(edgeSize + 1)
            } else {
                edgeSize
            }
        }
        return when (scan(position)) {
            STATIONARY -> 0
            else -> maxCubeSizeFromPosition(1)
        }
    }
    fun findFirstBeingPulledCellColumn(): Int {
        return generateSequence(0, 1::plus)
            .filter { col -> scan(Position(col, row)) == BEING_PULLED }
            .first()
    }
    fun maxCubeSizeInRow(startColumn: Int): Pair<Position, Int> {
        var prevPos = Position(startColumn, row)
        var prevValue = maxCubeSizeFromPosition(prevPos)
        var pos: Position = prevPos.plusX(1)
        var value: Int = maxCubeSizeFromPosition(pos)
        while (value > prevValue) {
            prevPos = pos
            prevValue = value
            pos = prevPos.plusX(1)
            value = maxCubeSizeFromPosition(pos)
        }
        return prevPos to prevValue
    }
    return maxCubeSizeInRow(findFirstBeingPulledCellColumn())
}
