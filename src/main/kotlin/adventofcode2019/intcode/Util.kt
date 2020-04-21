package adventofcode2019.intcode

import adventofcode2019.linesFromResource
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