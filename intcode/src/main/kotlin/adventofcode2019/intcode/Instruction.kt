package adventofcode2019.intcode

import adventofcode2019.intcode.IntCodeNumber.Companion
import adventofcode2019.intcode.Parameter.BasicParameter
import adventofcode2019.intcode.Parameter.ComplexParameter
import adventofcode2019.intcode.ParameterMode.IMMEDIATE_MODE
import adventofcode2019.intcode.ParameterMode.POSITION_MODE
import adventofcode2019.intcode.ParameterMode.RELATIVE
import adventofcode2019.intcode.State.HALTED
import adventofcode2019.intcode.State.RUNNING

interface Instruction {
    fun paramsCount(): Int
    suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters)
}

private fun Parameter.resolveValue(intCodeState: IntCodeState): IntCodeNumber {
    fun ComplexParameter.resolveValue(intCodeState: IntCodeState): IntCodeNumber = when (parameterMode) {
        POSITION_MODE -> intCodeState.memory[value.toInt()]
        IMMEDIATE_MODE -> value
        RELATIVE -> intCodeState.memory[intCodeState.relativeBase + value.toInt()]
    }
    return when (this) {
        is BasicParameter -> intCodeState.memory[value.toInt()]
        is ComplexParameter -> resolveValue(intCodeState)
    }
}


private fun Parameter.resolveReference(intCodeState: IntCodeState): Int {
    fun ComplexParameter.resolveReference(intCodeState: IntCodeState): Int = when (parameterMode) {
        POSITION_MODE -> value.toInt()
        IMMEDIATE_MODE -> value.toInt()
        RELATIVE -> intCodeState.relativeBase + value.toInt()
    }
    return when (this) {
        is BasicParameter -> value.toInt()
        is ComplexParameter -> resolveReference(intCodeState)
    }
}

object HaltInstruction : Instruction {
    override fun paramsCount(): Int = 0
    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.state = HALTED
    }
}

object AddInstruction : Instruction {
    override fun paramsCount(): Int = 3
    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].resolveReference(intCodeState)] =
            params[0].resolveValue(intCodeState) + params[1].resolveValue(intCodeState)
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object MultiplyInstruction : Instruction {
    override fun paramsCount(): Int = 3
    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].resolveReference(intCodeState)] =
            params[0].resolveValue(intCodeState) * params[1].resolveValue(intCodeState)
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object ReadLineInputInstruction : Instruction {
    private val inputInstruction: Instruction = InputInstruction(object : InputProvider {
        override suspend fun get(): IntCodeNumber {
            return readLine()?.let { line ->
                IntCodeNumber.of(line)
            } ?: throw IllegalArgumentException("Line is null")
        }
    })

    override fun paramsCount(): Int = inputInstruction.paramsCount()
    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) =
        inputInstruction.execute(intCodeState, params)
}

class InputInstruction(private val inputProvider: InputProvider) : Instruction {
    override fun paramsCount(): Int = 1

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        val input = inputProvider.get()
        intCodeState.memory[params[0].resolveReference(intCodeState)] = input
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

interface InputProvider {
    suspend fun get(): IntCodeNumber
}

object PrintlnOutputInstruction : Instruction {
    private val outputInstruction: Instruction = OutputInstruction(
        object : OutputConsumer {
            override suspend fun consume(output: IntCodeNumber) = println(output)
        }
    )

    override fun paramsCount(): Int = outputInstruction.paramsCount()

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) =
        outputInstruction.execute(intCodeState, params)
}

class OutputInstruction(private val outputConsumer: OutputConsumer) : Instruction {
    override fun paramsCount(): Int = 1

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        outputConsumer.consume(params[0].resolveValue(intCodeState))
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

interface OutputConsumer {
    suspend fun consume(output: IntCodeNumber)
}

object JumpIfTrueInstruction : Instruction {
    override fun paramsCount(): Int = 2

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.position = when {
            params[0].resolveValue(intCodeState) != IntCodeNumber.ZERO -> params[1].resolveValue(intCodeState).toInt()
            else -> intCodeState.position + paramsCount() + 1
        }
        intCodeState.state = RUNNING
    }
}

object JumpIfFalseInstruction : Instruction {
    override fun paramsCount(): Int = 2

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.position = when {
            params[0].resolveValue(intCodeState) == Companion.ZERO -> params[1].resolveValue(intCodeState).toInt()
            else -> intCodeState.position + paramsCount() + 1
        }
        intCodeState.state = RUNNING
    }
}

object LessThanInstruction : Instruction {
    override fun paramsCount(): Int = 3

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].resolveReference(intCodeState)] = when {
            params[0].resolveValue(intCodeState) < params[1].resolveValue(intCodeState) -> IntCodeNumber.ONE
            else -> IntCodeNumber.ZERO
        }
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object EqualsInstruction : Instruction {
    override fun paramsCount(): Int = 3

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.memory[params[2].resolveReference(intCodeState)] = when {
            params[0].resolveValue(intCodeState) == params[1].resolveValue(intCodeState) -> IntCodeNumber.ONE
            else -> Companion.ZERO
        }
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}

object AdjustRelativeBaseInstruction : Instruction {
    override fun paramsCount(): Int = 1

    override suspend fun execute(intCodeState: IntCodeState, params: InstructionParameters) {
        intCodeState.relativeBase += params[0].resolveValue(intCodeState).toInt()
        intCodeState.state = RUNNING
        intCodeState.position += paramsCount() + 1
    }
}
