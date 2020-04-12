package adventofcode2019.day05

import adventofcode2019.intcode.AddInstruction
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.BasicCommandReader
import adventofcode2019.intcode.Code
import adventofcode2019.intcode.ComplexCommandReader
import adventofcode2019.intcode.HaltInstruction
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.Intcode
import adventofcode2019.intcode.MapInstructionReader
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.MultiplyInstruction
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.runUntilHalted
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.streams.asSequence

fun main() {
    createAndRunIntcode()
}

fun createAndRunIntcode(): Intcode {
    val intcode = createIntcode(ArrayMemory.fromSequence(getInput()))
    intcode.runUntilHalted()
    return intcode
}

fun createIntcode(memory: Memory): Intcode {
    return Intcode(
        memory,
        ComplexCommandReader(
            MapInstructionReader(
                mapOf(
                    Code(1) to AddInstruction,
                    Code(2) to MultiplyInstruction,
                    Code(3) to InputInstruction,
                    Code(4) to OutputInstruction,
                    Code(99) to HaltInstruction
                )
            )
        )
    )
}

fun getInput(): Sequence<Int> {
    return linesFromResource(Paths.get("adventofcode2019", "day05", "input.txt"))
        .asSequence()
        .flatMap {
            it.split(",").asSequence()
                .map(String::trim)
                .map(String::toInt)
        }
}