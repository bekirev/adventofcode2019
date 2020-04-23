package adventofcode2019.intcode

import adventofcode2019.linesFromResource
import kotlinx.coroutines.channels.Channel
import java.nio.file.Path
import kotlin.streams.asSequence

fun createIntCodeAllInstr(
    memory: Memory,
    inputInstruction: Instruction = ReadLineInputInstruction,
    outputInstruction: Instruction = PrintlnOutputInstruction
): IntCode {
    return IntCode(
        memory,
        HybridCommandReader(
            MapInstructionReader(
                mapOf(
                    Code(1) to AddInstruction,
                    Code(2) to MultiplyInstruction,
                    Code(3) to inputInstruction,
                    Code(4) to outputInstruction,
                    Code(5) to JumpIfTrueInstruction,
                    Code(6) to JumpIfFalseInstruction,
                    Code(7) to LessThanInstruction,
                    Code(8) to EqualsInstruction,
                    Code(9) to AdjustRelativeBaseInstruction,
                    Code(99) to HaltInstruction
                )
            )
        )
    )
}

fun getIntCodeInput(path: Path): Sequence<IntCodeNumber> {
    return linesFromResource(path)
        .asSequence()
        .flatMap {
            it.split(",").asSequence()
                .map(String::trim)
                .map { str -> IntCodeNumber.fromString(str) }
                .filterNotNull()
        }
}

class ChannelInputProvider(private val channel: Channel<IntCodeNumber>) : InputProvider {
    override suspend fun get(): IntCodeNumber = channel.receive()
}

class ChannelOutputConsumer(private val channel: Channel<IntCodeNumber>) : OutputConsumer {
    override suspend fun consume(output: IntCodeNumber) = channel.send(output)
}

class ConstantInputProvider(private val value: IntCodeNumber) : InputProvider {
    override suspend fun get(): IntCodeNumber = value
}

interface SendBus<in T> {
    suspend fun send(value: T)
}

interface ReceiveBus<out T> {
    suspend fun receive(): T
}

interface MonoBus<T> : SendBus<T>, ReceiveBus<T>

class TransformingInputProvider<T>(
    private val bus: MonoBus<IntCodeNumber>,
    private val transformation: (T) -> IntCodeNumber
) : SendBus<T>, InputProvider {
    override suspend fun send(value: T) = bus.send(transformation(value))
    override suspend fun get(): IntCodeNumber = bus.receive()
}

class TransformingOutputConsumer<T>(
    private val bus: MonoBus<IntCodeNumber>,
    private val transformation: (IntCodeNumber) -> T
) : ReceiveBus<T>, OutputConsumer {
    override suspend fun receive(): T = transformation(bus.receive())
    override suspend fun consume(output: IntCodeNumber) = bus.send(output)
}

class ChannelMonoBus<T>(private val channel: Channel<T>) : MonoBus<T> {
    override suspend fun send(value: T) = channel.send(value)

    override suspend fun receive(): T = channel.receive()
}
