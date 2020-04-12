package adventofcode2019.intcode

import adventofcode2019.intcode.State.RUNNING

class Intcode private constructor(
    private val intCodeState: IntCodeState,
    private val commandReader: CommandReader
) {
    constructor(memory: Memory, commandReader: CommandReader) : this(IntCodeState(memory, 0, RUNNING), commandReader)

    fun memoryAt(index: Int): Int {
        return intCodeState.memory[index]
    }

    fun step(): State {
        val (command, positionsRead) = commandReader.read(intCodeState)
        command.execute(intCodeState)
        return intCodeState.state
    }
}

data class IntCodeState(val memory: Memory, var position: Int, var state: State)

enum class State {
    RUNNING,
    HALTED
}

fun Intcode.runUntilHalted(): Intcode = this.apply {
    while (step() != State.HALTED) {}
}
