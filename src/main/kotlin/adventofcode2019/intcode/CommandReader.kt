package adventofcode2019.intcode

import adventofcode2019.intcode.Parameter.BasicParameter
import adventofcode2019.intcode.Parameter.ComplexParameter
import adventofcode2019.intcode.ParameterMode.IMMEDIATE_MODE
import adventofcode2019.intcode.ParameterMode.POSITION_MODE

interface CommandReader {
    fun read(intCodeState: IntCodeState): CommandReadResult
}

class BasicCommandReader(private val instructionReader: InstructionReader) : CommandReader {
    private val commandParser: CommandParser = BasicCommandParser(instructionReader)

    override fun read(intCodeState: IntCodeState): CommandReadResult {
        val code = Code(intCodeState.memory[intCodeState.position])
        return commandParser.read(intCodeState, code)
    }
}

private fun read(
    intCodeState: IntCodeState,
    code: Code,
    instruction: Instruction,
    parameterParser: (IntCodeState, Code, Int) -> Parameter
): CommandReadResult {
    val paramsCount = instruction.paramsCount()
    val args = when {
        paramsCount > 0 -> {
            val paramsList: MutableList<Parameter> = ArrayList(paramsCount)
            for (i in 0 until paramsCount) {
                paramsList.add(parameterParser(intCodeState, code, i))
            }
            ArrayInstructionParameters.fromList(paramsList)
        }
        else -> EmptyInstructionParameters
    }
    return CommandReadResult(
        Command(instruction, args),
        paramsCount + 1
    )
}

class HybridCommandReader(private val instructionReader: InstructionReader) : CommandReader {
    private val basicCommandParser: CommandParser = BasicCommandParser(instructionReader)
    private val complexCommandParser: CommandParser = ComplexCommandParser(instructionReader)

    override fun read(intCodeState: IntCodeState): CommandReadResult {
        val code = Code(intCodeState.memory[intCodeState.position])
        return when {
            code.length() < 3 -> basicCommandParser.read(intCodeState, code)
            else -> complexCommandParser.read(intCodeState, code)
        }
    }
}

private interface CommandParser {
    fun read(intCodeState: IntCodeState, code: Code): CommandReadResult
}

private class BasicCommandParser(private val instructionReader: InstructionReader) : CommandParser {
    override fun read(intCodeState: IntCodeState, code: Code): CommandReadResult {
        val instruction = instructionReader.read(code)
        return read(intCodeState, code, instruction) { intCodeState, _, i ->
            BasicParameter(intCodeState.memory[intCodeState.position + i + 1])
        }
    }
}

private class ComplexCommandParser(private val instructionReader: InstructionReader) : CommandParser {
    override fun read(intCodeState: IntCodeState, code: Code): CommandReadResult {
        val instruction = instructionReader.read(Code(code.first(2)))
        return read(intCodeState, code, instruction) { intCodeState, code, i ->
            ComplexParameter(
                intCodeState.memory[intCodeState.position + i + 1],
                code[i + 2].let { paramModeCode ->
                    when (paramModeCode) {
                        0 -> POSITION_MODE
                        1 -> IMMEDIATE_MODE
                        else -> throw IllegalArgumentException("Unknown parameter mode code: $paramModeCode")
                    }
                }
            )
        }
    }
}

data class CommandReadResult(val command: Command, val positionsRead: Int)
