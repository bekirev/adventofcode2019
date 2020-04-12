package adventofcode2019.day05

import adventofcode2019.intcode.AddInstruction
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.Code
import adventofcode2019.intcode.Equals
import adventofcode2019.intcode.HaltInstruction
import adventofcode2019.intcode.HybridCommandReader
import adventofcode2019.intcode.ReadLineInputInstruction
import adventofcode2019.intcode.Intcode
import adventofcode2019.intcode.JumpIfFalse
import adventofcode2019.intcode.JumpIfTrue
import adventofcode2019.intcode.LessThan
import adventofcode2019.intcode.MapInstructionReader
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.MultiplyInstruction
import adventofcode2019.intcode.PrintlnOutputInstruction
import adventofcode2019.intcode.runUntilHalted
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.streams.asSequence

fun main() {
    createAndRunIntcode()
}

fun createAndRunIntcode(): Intcode {
    return createIntcode(ArrayMemory.fromSequence(getInput())).runUntilHalted()
}

fun createIntcode(memory: Memory): Intcode {
    return Intcode(
        memory,
        HybridCommandReader(
            MapInstructionReader(
                mapOf(
                    Code(1) to AddInstruction,
                    Code(2) to MultiplyInstruction,
                    Code(3) to ReadLineInputInstruction,
                    Code(4) to PrintlnOutputInstruction,
                    Code(5) to JumpIfTrue,
                    Code(6) to JumpIfFalse,
                    Code(7) to LessThan,
                    Code(8) to Equals,
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