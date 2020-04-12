package adventofcode2019.intcode

class Intcode private constructor(
    private val memory: Memory,
    private val commandReader: CommandReader,
    private var position: Int,
    private var state: State
) {
    constructor(memory: Memory, commandReader: CommandReader) : this(memory, commandReader, 0, State.RUNNING)

    fun memoryAt(index: Int): Int {
        return memory[index]
    }

    fun step(): State {
        val (command, positionsRead) = commandReader.read(memory, position)
        state = command.execute(memory)
        position += positionsRead
        return state
    }
}

enum class State {
    RUNNING,
    HALTED
}

fun Intcode.runUntilHalted() {
    while (step() != State.HALTED) {}
}
