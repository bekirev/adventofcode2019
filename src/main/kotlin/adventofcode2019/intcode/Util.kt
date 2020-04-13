package adventofcode2019.intcode

import adventofcode2019.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun createIntcode(memory: Memory): Intcode {
    return createIntcode(memory, ReadLineInputInstruction, PrintlnOutputInstruction)
}

fun createIntcode(memory: Memory, inputInstruction: Instruction, outputInstruction: Instruction): Intcode {
    return Intcode(
        memory,
        HybridCommandReader(
            MapInstructionReader(
                mapOf(
                    Code(1) to AddInstruction,
                    Code(2) to MultiplyInstruction,
                    Code(3) to inputInstruction,
                    Code(4) to outputInstruction,
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

fun getInput(path: Path): Sequence<Int> {
    return linesFromResource(path)
        .asSequence()
        .flatMap {
            it.split(",").asSequence()
                .map(String::trim)
                .map(String::toInt)
        }
}