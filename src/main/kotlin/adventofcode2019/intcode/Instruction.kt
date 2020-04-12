package adventofcode2019.intcode

import adventofcode2019.intcode.State.HALTED
import adventofcode2019.intcode.State.RUNNING
import kotlin.IllegalArgumentException

interface Instruction {
    fun argsCount(): Int
    fun execute(args: InstructionArguments, memory: Memory): State
}

object HaltInstruction : Instruction {
    override fun argsCount(): Int = 0
    override fun execute(args: InstructionArguments, memory: Memory): State = HALTED
}

object AddInstruction : Instruction {
    override fun argsCount(): Int = 3
    override fun execute(args: InstructionArguments, memory: Memory): State {
        memory[args[2]] = args[0] + args[1]
        return RUNNING
    }
}

object MultiplyInstruction : Instruction {
    override fun argsCount(): Int = 3
    override fun execute(args: InstructionArguments, memory: Memory): State {
        memory[args[2]] = args[0] * args[1]
        return RUNNING
    }
}

object InputInstruction: Instruction {
    override fun argsCount(): Int = 1

    override fun execute(args: InstructionArguments, memory: Memory): State {
        val input = readLine()?.toInt() ?: throw IllegalArgumentException("Line is null")
        memory[args[0]] = input
        return RUNNING
    }
}

object OutputInstruction: Instruction {
    override fun argsCount(): Int = 1

    override fun execute(args: InstructionArguments, memory: Memory): State {
        println(memory[args[0]])
        return RUNNING
    }
}

interface InstructionArguments {
    operator fun get(index: Int): Int
}

object EmptyInstructionArguments : InstructionArguments {
    override fun get(index: Int): Int {
        throw IllegalArgumentException("No argument at index: $index")
    }
}

class ArrayInstructionArguments(private val args: Array<Int>) : InstructionArguments {
    override fun get(index: Int): Int = args[index]
}
