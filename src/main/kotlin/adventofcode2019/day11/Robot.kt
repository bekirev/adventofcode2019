package adventofcode2019.day11

import adventofcode2019.day11.Color.BLACK
import adventofcode2019.day11.Color.WHITE
import adventofcode2019.day11.TurnDirection.LEFT
import adventofcode2019.day11.TurnDirection.RIGHT
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ChannelOutputConsumer
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.SendBus
import adventofcode2019.intcode.TransformingInputProvider
import adventofcode2019.intcode.asMonoBus
import adventofcode2019.intcode.createIntCodeAllInstr
import kotlinx.coroutines.channels.Channel

internal class Robot private constructor(
    private val intCode: IntCode,
    private val robotInput: SendBus<Color>,
    private val robotOutput: Channel<IntCodeNumber>
) {
    companion object {
        fun fromMemory(memorySeq: Sequence<IntCodeNumber>): Robot {
            val transformingInputProvider = TransformingInputProvider(
                Channel<IntCodeNumber>(Channel.UNLIMITED).asMonoBus(),
                Color::toIntCodeNumber
            )
            val outputChannel = Channel<IntCodeNumber>(Channel.UNLIMITED)
            val intCode = createIntCodeAllInstr(
                AdditionalMapMemory.fromMemory(ArrayMemory.fromSequence(memorySeq)),
                InputInstruction(transformingInputProvider),
                OutputInstruction(ChannelOutputConsumer(outputChannel))
            )
            return Robot(intCode, transformingInputProvider, outputChannel)
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
            robotOutput.receive().toColor(),
            robotOutput.receive().toTurnDirection()
        )
    }

    suspend fun panelColor(color: Color) {
        robotInput.send(color)
    }
}

private fun Color.toIntCodeNumber(): IntCodeNumber = when (this) {
    BLACK -> IntCodeNumber.ZERO
    WHITE -> IntCodeNumber.ONE
}

internal data class RobotOutput(val paintColor: Color, val turnDirection: TurnDirection)

internal enum class TurnDirection {
    LEFT, RIGHT
}