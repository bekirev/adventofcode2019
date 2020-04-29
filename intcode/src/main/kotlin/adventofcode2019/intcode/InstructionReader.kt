package adventofcode2019.intcode

import adventofcode2019.digitsCount
import adventofcode2019.first
import adventofcode2019.getDigitAt

interface InstructionReader {
    fun read(code: Code): Instruction
}

class MapInstructionReader(private val commands: Map<Code, Instruction>) : InstructionReader {
    override fun read(code: Code): Instruction {
        return commands[code] ?: throw IllegalArgumentException("Unknown command code: $code")
    }
}

data class Code(private val code: Int) {
    operator fun get(index: Int): Int = code.getDigitAt(index)
    fun first(n: Int): Int = code.first(n)
    fun length(): Int = code.digitsCount()
}
