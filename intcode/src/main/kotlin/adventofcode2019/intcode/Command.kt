package adventofcode2019.intcode

class Command(private val instruction: Instruction, private val params: InstructionParameters) {
    suspend fun execute(intCodeState: IntCodeState) = instruction.execute(intCodeState, params)
}
