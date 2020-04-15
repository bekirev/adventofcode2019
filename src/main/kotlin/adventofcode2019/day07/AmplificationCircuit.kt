package adventofcode2019.day07

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.InputProvider
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import adventofcode2019.intcode.getIntCodeInput
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val initialMemory = ArrayMemory.fromList(
        getIntCodeInput(Paths.get("adventofcode2019", "day07", "input.txt")).toList()
    )
    println(findMaxThrusterSignal(initialMemory.copyOf()))
    println(findMaxThrusterSignalWithFeedback(initialMemory.copyOf()))
}

data class ThrusterSignalResult(
    val signal: IntCodeNumber,
    val phaseSettings: PhaseSettings
)

data class PhaseSettings(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int,
    val e: Int
)

fun findMaxThrusterSignal(initialMemory: ArrayMemory): ThrusterSignalResult {
    return findMaxThrusterSignal(0..4, initialMemory) { initMemory, input ->
        runAmplificationCircuit(initMemory, input)
    }
}

fun findMaxThrusterSignalWithFeedback(initialMemory: ArrayMemory): ThrusterSignalResult {
    return findMaxThrusterSignal(5..9, initialMemory) { initMemory, input ->
        runAmplificationCircuitWithFeedback(initMemory, input)
    }
}

private fun findMaxThrusterSignal(
    phaseRange: IntRange,
    initialMemory: ArrayMemory,
    runner: (ArrayMemory, PhaseSettings) -> IntCodeNumber
): ThrusterSignalResult {
    return phases(phaseRange)
        .map { input ->
            ThrusterSignalResult(
                runner(initialMemory, input),
                input
            )
        }
        .onEach(::println)
        .maxBy { it.signal }!!
}

private fun phases(phaseRange: IntRange): Sequence<PhaseSettings> = sequence {
    for (a in phaseRange) {
        for (b in phaseRange) {
            if (b == a) continue
            for (c in phaseRange) {
                if (c == a || c == b) continue
                for (d in phaseRange) {
                    if (d == a || d == b || d == c) continue
                    for (e in phaseRange) {
                        if (e == a || e == b || e == c || e == d) continue
                        yield(PhaseSettings(a, b, c, d, e))
                    }
                }
            }
        }
    }
}

internal fun runAmplificationCircuit(initialMemory: ArrayMemory, phaseSettings: PhaseSettings): IntCodeNumber {
    return runAmplificationCircuit(
        initialMemory,
        phaseSettings,
        QueueInputOutputImpl("->A"),
        QueueInputOutputImpl("E->")
    )
}

internal fun runAmplificationCircuitWithFeedback(
    initialMemory: ArrayMemory,
    phaseSettings: PhaseSettings
): IntCodeNumber {
    val aInput = OutputListenerQueueInputOutput(QueueInputOutputImpl("E->A"), ::println)
    return runAmplificationCircuit(initialMemory, phaseSettings, aInput, aInput)
}

private fun runAmplificationCircuit(
    initialMemory: ArrayMemory,
    phaseSettings: PhaseSettings,
    aInput: QueueInputOutput,
    eOutput: QueueInputOutput
): IntCodeNumber = runBlocking {
    println(phaseSettings)
    runAmplificationCircuit(
        initialMemory,
        phaseSettings,
        aInput = aInput.put(IntCodeNumber.ZERO),
        aOutputBInput = QueueInputOutputImpl("A->B"),
        bOutputCInput = QueueInputOutputImpl("B->C"),
        cOutputDInput = QueueInputOutputImpl("C->D"),
        dOutputEInput = QueueInputOutputImpl("D->E"),
        eOutput = eOutput
    )
}

private suspend fun runAmplificationCircuit(
    initialMemory: ArrayMemory,
    phaseSettings: PhaseSettings,
    aInput: QueueInputOutput,
    aOutputBInput: QueueInputOutput,
    bOutputCInput: QueueInputOutput,
    cOutputDInput: QueueInputOutput,
    dOutputEInput: QueueInputOutput,
    eOutput: QueueInputOutput
): IntCodeNumber {
    fun intCode(phase: Int, input: InputProvider, output: OutputConsumer): IntCode {
        return createIntCodeAllInstr(
            initialMemory.copyOf(),
            InputInstruction(
                TwoSequentialInputProvider(
                    object : InputProvider {
                        override suspend fun get(): IntCodeNumber = IntCodeNumber.fromInt(phase)
                    },
                    input
                )
            ),
            OutputInstruction(output)
        )
    }
    return coroutineScope {
        val job = launch {
            val a = intCode(phaseSettings.a, aInput, aOutputBInput)
            a.run()
            val b = intCode(phaseSettings.b, aOutputBInput, bOutputCInput)
            b.run()
            val c = intCode(phaseSettings.c, bOutputCInput, cOutputDInput)
            c.run()
            val d = intCode(phaseSettings.d, cOutputDInput, dOutputEInput)
            d.run()
            val e = intCode(phaseSettings.e, dOutputEInput, eOutput)
            e.run()
        }
        job.join()
        eOutput.get()
    }
}


class TwoSequentialInputProvider(private val first: InputProvider, private val second: InputProvider) : InputProvider {
    private val invokeCount = AtomicInteger(0)
    override suspend fun get(): IntCodeNumber {
        val count = invokeCount.getAndIncrement()
        return if (count % 2 == 0) {
            first.get()
        } else {
            second.get()
        }
    }
}

interface QueueInputOutput : InputProvider, OutputConsumer {
    suspend fun put(value: IntCodeNumber): QueueInputOutput
}

class QueueInputOutputImpl(private val name: String = "") : QueueInputOutput {
    private val queue: Channel<IntCodeNumber> = Channel(Channel.UNLIMITED)

    override suspend fun get(): IntCodeNumber {
        return queue.receive()
    }

    override suspend fun consume(output: IntCodeNumber) {
        queue.send(output)
    }

    override suspend fun put(value: IntCodeNumber): QueueInputOutput {
        queue.send(value)
        return this
    }
}

class OutputListenerQueueInputOutput(
    private val queueInputOutput: QueueInputOutput,
    private val outputListener: ((IntCodeNumber) -> Unit)
) : QueueInputOutput by queueInputOutput {
    override suspend fun consume(output: IntCodeNumber) {
        outputListener(output)
        queueInputOutput.consume(output)
    }
}
