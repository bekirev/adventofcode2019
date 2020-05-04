package adventofcode2019.day02

import adventofcode2019.intcode.AddInstruction
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.BasicCommandReader
import adventofcode2019.intcode.Code
import adventofcode2019.intcode.HaltInstruction
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.MapInstructionReader
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.MultiplyInstruction
import adventofcode2019.intcode.intCodeInput
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main() {
    val memory = ArrayMemory.of(
        Paths.get("adventofcode2019", "day02", "input.txt")
            .intCodeInput()
    )
    println(initMemoryAndRunIntCode(memory.copyOf(), 12, 2).memoryAt(0))
    loop@ for (noun in 0..99) {
        for (verb in 0..99) {
            val result = initMemoryAndRunIntCode(memory.copyOf(), noun, verb).memoryAt(0)
            if (result == IntCodeNumber.of(19690720)) {
                println("noun: $noun, verb: $verb, result: ${100 * noun + verb}")
                break@loop
            }
        }
    }
}

private fun initMemoryAndRunIntCode(memory: Memory, noun: Int, verb: Int): IntCode {
    memory[1] = IntCodeNumber.of(noun)
    memory[2] = IntCodeNumber.of(verb)
    return runIntCode(memory)
}

internal fun runIntCode(memory: Memory): IntCode = runBlocking {
    val intCode = createIntCode(memory)
    intCode.run()
    intCode
}

private fun createIntCode(memory: Memory): IntCode {
    return IntCode(
        memory,
        BasicCommandReader(
            MapInstructionReader(
                mapOf(
                    Code(1) to AddInstruction,
                    Code(2) to MultiplyInstruction,
                    Code(99) to HaltInstruction
                )
            )
        )
    )
}