package adventofcode2019.day11

import adventofcode2019.day11.Color.BLACK
import adventofcode2019.day11.Color.WHITE
import adventofcode2019.day11.TurnDirection.LEFT
import adventofcode2019.day11.TurnDirection.RIGHT
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.InputProvider
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import kotlinx.coroutines.channels.Channel

class Robot private constructor(
    private val intCode: IntCode,
    private val inputChannel: Channel<IntCodeNumber>,
    private val outputChannel: Channel<IntCodeNumber>
) {
    companion object {
        fun fromMemory(memorySeq: Sequence<IntCodeNumber>): Robot {
            val inputChannel = Channel<IntCodeNumber>(Channel.UNLIMITED)
            val outputChannel = Channel<IntCodeNumber>(Channel.UNLIMITED)
            val intCode = createIntCodeAllInstr(
                AdditionalMapMemory.fromMemory(ArrayMemory.fromSequence(memorySeq)),
                InputInstruction(
                    object : InputProvider {
                        override suspend fun get(): IntCodeNumber {
                            return inputChannel.receive()
                        }
                    }
                ),
                OutputInstruction(
                    object : OutputConsumer {
                        override suspend fun consume(output: IntCodeNumber) {
                            outputChannel.send(output)
                        }
                    }
                )
            )
            return Robot(intCode, inputChannel, outputChannel)
        }
    }

    suspend fun run() {
        intCode.run()
    }

    suspend fun output(): RobotOutput {
        fun IntCodeNumber.toColor(): Color = when (this) {
            IntCodeNumber.ZERO -> BLACK
            IntCodeNumber.ONE -> WHITE
            else -> throw IllegalArgumentException("Unknown color code")
        }

        fun IntCodeNumber.toTurnDirection(): TurnDirection = when (this) {
            IntCodeNumber.ZERO -> LEFT
            IntCodeNumber.ONE -> RIGHT
            else -> throw IllegalArgumentException("Unknown turn direction code")
        }
        return RobotOutput(
            outputChannel.receive().toColor(),
            outputChannel.receive().toTurnDirection()
        )
    }

    suspend fun panelColor(color: Color) {
        fun Color.toIntCodeNumber(): IntCodeNumber = when (this) {
            BLACK -> IntCodeNumber.ZERO
            WHITE -> IntCodeNumber.ONE
        }
        inputChannel.send(color.toIntCodeNumber())
    }
}

data class RobotOutput(val paintColor: Color, val turnDirection: TurnDirection)

enum class TurnDirection {
    LEFT, RIGHT
}