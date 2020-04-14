package adventofcode2019.intcode

import adventofcode2019.intcode.State.HALTED
import adventofcode2019.intcode.State.RUNNING

class IntCode private constructor(
    private val intCodeState: IntCodeState,
    private val commandReader: CommandReader
) {
    constructor(memory: Memory, commandReader: CommandReader) : this(IntCodeState(memory, 0, RUNNING), commandReader)

    fun memoryAt(index: Int): Int {
        return intCodeState.memory[index]
    }

    suspend fun run() {
        while (intCodeState.state != HALTED) {
            val command = commandReader.read(intCodeState)
            command.execute(intCodeState)
        }
    }
}

data class IntCodeState(val memory: Memory, var position: Int, var state: State)

enum class State {
    RUNNING,
    HALTED
}
