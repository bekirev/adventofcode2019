package adventofcode2019.intcode

import adventofcode2019.intcode.ArgumentMode.IMMEDIATE_MODE
import adventofcode2019.intcode.ArgumentMode.POSITION_MODE

interface CommandReader {
    fun read(memory: Memory, position: Int): CommandReadResult
}

class BasicCommandReader(private val instructionReader: InstructionReader) : CommandReader {
    override fun read(memory: Memory, position: Int): CommandReadResult {
        val code = Code(memory[position])
        return read(code, memory, position)
    }

    internal fun read(code: Code, memory: Memory, position: Int): CommandReadResult {
        val instruction = instructionReader.read(code)
        val argsCount = instruction.argsCount()
        val args = when {
            argsCount > 0 -> {
                val argsArr = Array(argsCount) { 0 }
                for (i in 0 until argsCount - 1) argsArr[i] = memory[memory[position + i + 1]]
                argsArr[argsCount - 1] = memory[position + argsCount]
                ArrayInstructionArguments(argsArr)
            }
            else -> EmptyInstructionArguments
        }
        return CommandReadResult(
            Command(instruction, args),
            argsCount + 1
        )
    }
}

class ComplexCommandReader(private val instructionReader: InstructionReader) : CommandReader {
    private val basicCommandReader = BasicCommandReader(instructionReader)

    override fun read(memory: Memory, position: Int): CommandReadResult {
        val code = Code(memory[position])
        return when {
            code.length() < 3 -> basicCommandReader.read(code, memory, position)
            else -> read(code, memory, position)
        }
    }

    private fun read(code: Code, memory: Memory, position: Int): CommandReadResult {
        val instruction = instructionReader.read(Code(code.first(2)))
        val argsCount = instruction.argsCount()
        val args = when {
            argsCount > 0 -> {
                val argsArr = Array(argsCount) { 0 }
                for (i in 0 until argsCount - 1) {
                    val argMode = code[i + 2].let { argModeCode ->
                        when (argModeCode) {
                            0 -> POSITION_MODE
                            1 -> IMMEDIATE_MODE
                            else -> throw IllegalArgumentException("Unknown argument mode code: $argModeCode")
                        }
                    }
                    argsArr[i] = memory[position + i + 1].let { valueAtPos ->
                        when (argMode) {
                            POSITION_MODE -> memory[valueAtPos]
                            IMMEDIATE_MODE -> valueAtPos
                        }
                    }
                }
                argsArr[argsCount - 1] = memory[position + argsCount]
                ArrayInstructionArguments(argsArr)
            }
            else -> EmptyInstructionArguments
        }
        return CommandReadResult(
            Command(instruction, args),
            argsCount + 1
        )
    }
}

data class CommandReadResult(val command: Command, val positionsRead: Int)

enum class ArgumentMode {
    POSITION_MODE, IMMEDIATE_MODE
}
