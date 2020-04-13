package adventofcode2019.day02

import adventofcode2019.intcode.AddInstruction
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.BasicCommandReader
import adventofcode2019.intcode.Code
import adventofcode2019.intcode.HaltInstruction
import adventofcode2019.intcode.Intcode
import adventofcode2019.intcode.MapInstructionReader
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.MultiplyInstruction
import adventofcode2019.intcode.runUntilHalted
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.streams.asSequence

fun main() {
    println(createAndRunIntcode(12, 2).memoryAt(0))
    loop@ for (noun in 0..99) {
        for (verb in 0..99) {
            val result = createAndRunIntcode(noun, verb).memoryAt(0)
            if (result == 19690720) {
                println("noun: $noun, verb: $verb, result: ${100 * noun + verb}")
                break@loop
            }
        }
    }
}

private fun createAndRunIntcode(noun: Int, verb: Int): Intcode {
    return createIntcode(ArrayMemory.fromSequence(getInput()).inited(noun, verb)).runUntilHalted()
}

fun createIntcode(memory: Memory): Intcode {
    return Intcode(
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

private fun Memory.inited(noun: Int, verb: Int): Memory = this.apply {
    this[1] = noun
    this[2] = verb
}

fun getInput(): Sequence<Int> {
    return linesFromResource(Paths.get("adventofcode2019", "day02", "input.txt"))
        .asSequence()
        .flatMap {
            it.split(",").asSequence()
                .map(String::trim)
                .map(String::toInt)
        }
}
