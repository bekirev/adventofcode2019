package adventofcode2019.intcode

import adventofcode2019.intcode.State.HALTED
import adventofcode2019.intcode.State.RUNNING

class IntCode private constructor(
    private val intCodeState: IntCodeState,
    private val commandReader: CommandReader
) {
    constructor(memory: Memory, commandReader: CommandReader) : this(IntCodeState(memory, 0, RUNNING, 0), commandReader)

    fun memoryAt(index: Int): IntCodeNumber {
        return intCodeState.memory[index]
    }

    suspend fun run() {
        while (intCodeState.state != HALTED) {
            val command = commandReader.read(intCodeState)
            command.execute(intCodeState)
        }
    }
}

data class IntCodeState(val memory: Memory, var position: Int, var state: State, var relativeBase: Int)

enum class State {
    RUNNING,
    HALTED
}
