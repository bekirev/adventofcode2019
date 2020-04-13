package adventofcode2019.day07

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.createIntcode
import adventofcode2019.intcode.getInput
import adventofcode2019.intcode.runUntilHalted
import java.nio.file.Paths

fun main() {
    println(
        findMaxThrusterSignal(
            ArrayMemory(
                getInput(Paths.get("adventofcode2019", "day07", "input.txt")).toList().toIntArray()
            ),
            0..4
        )
    )
}

data class ThrusterSignalResult(
    val signal: Int,
    val input: Input
)

data class Input(
    val a: Int,
    val b: Int,
    val c: Int,
    val d: Int,
    val e: Int
)

fun findMaxThrusterSignal(initialMemory: ArrayMemory, phaseRange: IntRange): ThrusterSignalResult {
    val results = HashSet<ThrusterSignalResult>()
    for (a in phaseRange) {
        for (b in phaseRange) {
            if (b == a) continue
            for (c in phaseRange) {
                if (c == a || c == b) continue
                for (d in phaseRange) {
                    if (d == a || d == b || d == c) continue
                    for (e in phaseRange) {
                        if (e == a || e == b || e == c || e == d) continue
                        val input = Input(a, b, c, d, e)
                        results.add(
                            ThrusterSignalResult(
                                runAmplificationCurcuit(initialMemory, input),
                                input
                            )
                        )
                    }
                }
            }
        }
    }
    return results.maxBy { it.signal }!!
}

private fun runAmplificationCurcuit(
    initialMemory: ArrayMemory,
    input: Input
): Int {
    var aOut: Int? = null
    createIntcode(
        initialMemory.copyOf(),
        InputInstruction(
            TwoSequentialInputProvider(
                input.a,
                0
            )
        ),
        OutputInstruction { output -> aOut = output }
    ).runUntilHalted()
    var bOut: Int? = null
    createIntcode(
        initialMemory.copyOf(),
        InputInstruction(
            TwoSequentialInputProvider(
                input.b,
                aOut ?: throw IllegalStateException("A output in null")
            )
        ),
        OutputInstruction { output -> bOut = output }
    ).runUntilHalted()
    var cOut: Int? = null
    createIntcode(
        initialMemory.copyOf(),
        InputInstruction(
            TwoSequentialInputProvider(
                input.c,
                bOut ?: throw IllegalStateException("B output in null")
            )
        ),
        OutputInstruction { output -> cOut = output }
    ).runUntilHalted()
    var dOut: Int? = null
    createIntcode(
        initialMemory.copyOf(),
        InputInstruction(
            TwoSequentialInputProvider(
                input.d,
                cOut ?: throw IllegalStateException("C output in null")
            )
        ),
        OutputInstruction { output -> dOut = output }
    ).runUntilHalted()
    var eOut: Int? = null
    createIntcode(
        initialMemory.copyOf(),
        InputInstruction(
            TwoSequentialInputProvider(
                input.e,
                dOut ?: throw IllegalStateException("D output in null")
            )
        ),
        OutputInstruction { output -> eOut = output }
    ).runUntilHalted()
    return eOut!!
}

class TwoSequentialInputProvider(val first: Int, val second: Int) : () -> Int {
    var invokeCount = 0
    override fun invoke(): Int {
        invokeCount.let { count ->
            if (count > 1) throw IllegalStateException("Shouldn't be invoked 3rd time")
            ++invokeCount
            return if (count == 0) {
                first
            } else {
                second
            }
        }
    }
}
