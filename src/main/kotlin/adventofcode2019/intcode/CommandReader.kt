package adventofcode2019.intcode

import adventofcode2019.intcode.Parameter.BasicParameter
import adventofcode2019.intcode.Parameter.ComplexParameter
import adventofcode2019.intcode.ParameterMode.IMMEDIATE_MODE
import adventofcode2019.intcode.ParameterMode.POSITION_MODE

interface CommandReader {
    fun read(intCodeState: IntCodeState): Command
}

class BasicCommandReader(instructionReader: InstructionReader) : CommandReader {
    private val commandParser: CommandParser = BasicCommandParser(instructionReader)

    override fun read(intCodeState: IntCodeState): Command {
        val code = Code(intCodeState.memory[intCodeState.position])
        return commandParser.read(intCodeState, code)
    }
}

private fun read(
    intCodeState: IntCodeState,
    code: Code,
    instruction: Instruction,
    parameterParser: (IntCodeState, Code, Int) -> Parameter
): Command {
    val paramsCount = instruction.paramsCount()
    val args = when {
        paramsCount > 0 -> {
            val params: Sequence<Parameter> = sequence {
                for (i in 0 until paramsCount) {
                    yield(parameterParser(intCodeState, code, i))
                }
            }
            ArrayInstructionParameters.fromSequence(params)
        }
        else -> EmptyInstructionParameters
    }
    return Command(instruction, args)
}

class HybridCommandReader(instructionReader: InstructionReader) : CommandReader {
    private val basicCommandParser: CommandParser = BasicCommandParser(instructionReader)
    private val complexCommandParser: CommandParser = ComplexCommandParser(instructionReader)

    override fun read(intCodeState: IntCodeState): Command {
        val code = Code(intCodeState.memory[intCodeState.position])
        return when {
            code.length() < 3 -> basicCommandParser.read(intCodeState, code)
            else -> complexCommandParser.read(intCodeState, code)
        }
    }
}

private interface CommandParser {
    fun read(intCodeState: IntCodeState, code: Code): Command
}

private class BasicCommandParser(private val instructionReader: InstructionReader) : CommandParser {
    override fun read(intCodeState: IntCodeState, code: Code): Command {
        val instruction = instructionReader.read(code)
        return read(intCodeState, code, instruction) { state, _, i ->
            BasicParameter(state.memory[state.position + i + 1])
        }
    }
}

private class ComplexCommandParser(private val instructionReader: InstructionReader) : CommandParser {
    override fun read(intCodeState: IntCodeState, code: Code): Command {
        val instruction = instructionReader.read(Code(code.first(2)))
        return read(intCodeState, code, instruction) { state, instructionCode, i ->
            ComplexParameter(
                state.memory[state.position + i + 1],
                instructionCode[i + 2].let { paramModeCode ->
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
