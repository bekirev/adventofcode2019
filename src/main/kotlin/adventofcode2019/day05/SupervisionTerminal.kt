package adventofcode2019.day05

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.InputProvider
import adventofcode2019.intcode.Instruction
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.PrintlnOutputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import adventofcode2019.intcode.getInput
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main() {
    val memory = ArrayMemory.fromSequence(getInput(Paths.get("adventofcode2019", "day05", "input.txt")))
    createAndRunIntCode(memory.copyOf(), 1)
    createAndRunIntCode(memory.copyOf(), 5)
}

private fun createAndRunIntCode(memory: Memory, input: Int) {
    return createAndRunIntCode(memory, input, PrintlnOutputInstruction)
}

fun createAndRunIntCode(memory: Memory, input: Int, outputInstruction: Instruction) {
    runBlocking {
        createIntCodeAllInstr(
            memory,
            InputInstruction(object : InputProvider { override suspend fun get(): Int = input }),
            outputInstruction
        ).run()
    }
}
