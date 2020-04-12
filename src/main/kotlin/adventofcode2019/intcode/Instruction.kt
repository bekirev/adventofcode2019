package adventofcode2019.intcode

import adventofcode2019.intcode.Parameter.ComplexParameter
import adventofcode2019.intcode.Parameter.BasicParameter
import adventofcode2019.intcode.ParameterMode.IMMEDIATE_MODE
import adventofcode2019.intcode.ParameterMode.POSITION_MODE
import adventofcode2019.intcode.State.HALTED
import adventofcode2019.intcode.State.RUNNING
import kotlin.IllegalArgumentException

interface Instruction {
    fun paramsCount(): Int
    fun execute(intCodeState: IntCodeState, params: InstructionParameters)
}

private fun Parameter.resolveValue(memory: Memory): Int {
    return when (this) {
        is BasicParameter -> memory[value]
        is ComplexParameter -> realValue(memory)
    }
}

private fun ComplexParameter.realValue(memory: Memory): Int {
    return when (parameterMode) {
        POSITION_MODE -> memory[value]
        IMMEDIATE_MODE -> value
    }
}

object HaltInstruction : Instruction {
    override fun paramsCount(): Int = 0
    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.state = HALTED
    }
}

object AddInstruction : Instruction {
    override fun paramsCount(): Int = 3
    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].value] = params[0].resolveValue(intCodeState.memory) + params[1].resolveValue(intCodeState.memory)
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object MultiplyInstruction : Instruction {
    override fun paramsCount(): Int = 3
    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].value] = params[0].resolveValue(intCodeState.memory) * params[1].resolveValue(intCodeState.memory)
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object ReadLineInputInstruction : Instruction {
    private val inputInstruction: Instruction = InputInstruction {
        readLine()?.toInt() ?: throw IllegalArgumentException("Line is null")
    }

    override fun paramsCount(): Int = inputInstruction.paramsCount()
    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) = inputInstruction.execute(intCodeState, params)
}

class InputInstruction(private val inputProvider: () -> Int) : Instruction {
    override fun paramsCount(): Int = 1

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        val input = inputProvider()
        intCodeState.memory[params[0].value] = input
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object PrintlnOutputInstruction : Instruction {
    private val outputInstruction: Instruction = OutputInstruction { output ->
        println(output)
    }
    override fun paramsCount(): Int = outputInstruction.paramsCount()

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) =
        outputInstruction.execute(intCodeState, params)
}

class OutputInstruction(private val outputConsumer: (Int) -> Unit) : Instruction {
    override fun paramsCount(): Int = 1

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        outputConsumer(params[0].resolveValue(intCodeState.memory))
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object JumpIfTrue : Instruction {
    override fun paramsCount(): Int = 2

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.position = when {
            params[0].resolveValue(intCodeState.memory) != 0 -> params[1].resolveValue(intCodeState.memory)
            else -> intCodeState.position + paramsCount() + 1
        }
        intCodeState.state = RUNNING
    }
}

object JumpIfFalse : Instruction {
    override fun paramsCount(): Int = 2

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.position = when {
            params[0].resolveValue(intCodeState.memory) == 0 -> params[1].resolveValue(intCodeState.memory)
            else -> intCodeState.position + paramsCount() + 1
        }
        intCodeState.state = RUNNING
    }
}

object LessThan : Instruction {
    override fun paramsCount(): Int = 3

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].value] = if (params[0].resolveValue(intCodeState.memory) < params[1].resolveValue(intCodeState.memory)) 1 else 0
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object Equals : Instruction {
    override fun paramsCount(): Int = 3

    override fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].value] = if (params[0].resolveValue(intCodeState.memory) == params[1].resolveValue(intCodeState.memory)) 1 else 0
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}
