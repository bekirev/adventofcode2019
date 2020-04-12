package adventofcode2019.intcode

class Command(private val instruction: Instruction, private val args: InstructionArguments) {
    fun execute(memory: Memory): State = instruction.execute(args, memory)
}
