package adventofcode2019.day02

import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.streams.asSequence

fun main() {
    println(createAndRunIntcode(12, 2).memoryAt(0))
    loop@ for (noun in 0..99) {
        for (verb in 0..99) {
            val result = createAndRunIntcode(noun, verb).memoryAt(0)
            if (result == 19690720) {
                println("noun: $noun, verb: $verb, result: ${100 * noun + verb}")
                break@loop
            }
        }
    }
}

fun createAndRunIntcode(noun: Int, verb: Int): Intcode {
    val memory = ArrayMemory(getInput())
    initMemory(memory, noun, verb)
    val intcode = Intcode(memory)
    while (intcode.step() == IntcodeState.RUNNING) {}
    return intcode
}

fun initMemory(memory: Memory, noun: Int, verb: Int) {
    memory[1] = noun
    memory[2] = verb
}

class Intcode private constructor(
    private val memory: Memory,
    private var position: Int,
    private var state: IntcodeState
) {
    constructor(memory: Memory) : this(memory, 0, IntcodeState.RUNNING)

    fun memoryAt(index: Int): Int {
        return memory[index]
    }

    fun step(): IntcodeState {
        val command = readCommand()
        implementCommand(command)
        state = when (command) {
            is Command.Halt -> IntcodeState.HALTED
            else -> IntcodeState.RUNNING
        }
        if (state != IntcodeState.HALTED) position += 4
        return state
    }

    private fun implementCommand(command: Command) {
        when (command) {
            is Command.Add -> memory[command.resultIndex] =
                memory[command.firstArgIndex] + memory[command.secondArgIndex]
            is Command.Multiply -> memory[command.resultIndex] =
                memory[command.firstArgIndex] * memory[command.secondArgIndex]
        }
    }

    private fun readCommand(): Command = position.let { curPos ->
        when (val commandCode = memory[curPos]) {
            1 -> Command.Add(memoryAt(curPos + 1), memoryAt(curPos + 2), memoryAt(curPos + 3))
            2 -> Command.Multiply(memoryAt(curPos + 1), memoryAt(curPos + 2), memoryAt(curPos + 3))
            99 -> Command.Halt
            else -> throw IllegalArgumentException("Unknown command code: $commandCode")
        }
    }
}

enum class IntcodeState {
    RUNNING,
    HALTED
}

interface Memory {
    operator fun get(index: Int): Int
    operator fun set(index: Int, value: Int)
}

class ArrayMemory private constructor(private val array: Array<Int>) : Memory {
    constructor(values: Sequence<Int>) : this(values.toList().toTypedArray())

    override fun get(index: Int): Int {
        return array[index]
    }

    override fun set(index: Int, value: Int) {
        array[index] = value
    }
}

sealed class Command(private val code: Int) {
    object Halt : Command(99)
    data class Add(val firstArgIndex: Int, val secondArgIndex: Int, val resultIndex: Int) : Command(1)
    data class Multiply(val firstArgIndex: Int, val secondArgIndex: Int, val resultIndex: Int) : Command(2)
}

fun getInput(): Sequence<Int> {
    return linesFromResource(Paths.get("adventofcode2019", "day02", "input.txt"))
        .asSequence()
        .flatMap {
            it.split(",").asSequence()
                .map(String::trim)
                .map(String::toInt)
        }
}
