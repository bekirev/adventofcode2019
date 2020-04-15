package adventofcode2019.day09

import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.ArrayMemory.Companion.copyOf
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.InputProvider
import adventofcode2019.intcode.IntCode
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import adventofcode2019.intcode.getIntCodeInput
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main() {
    val program = ArrayMemory.fromSequence(getIntCodeInput(Paths.get("adventofcode2019", "day09", "input.txt")))
    val intCodeFirstPart = intCode(program.copyOf(), IntCodeNumber.ONE)
    val intCodeSecondPart = intCode(program, IntCodeNumber.fromInt(2))
    runBlocking {
        intCodeFirstPart.run()
        intCodeSecondPart.run()
    }
}

private fun intCode(program: Memory, input: IntCodeNumber): IntCode {
    return createIntCodeAllInstr(
        AdditionalMapMemory.fromMemory(program),
        InputInstruction(
            object : InputProvider {
                override suspend fun get(): IntCodeNumber {
                    return input
                }
            }
        ),
        OutputInstruction(
            object : OutputConsumer {
                override suspend fun consume(output: IntCodeNumber) {
                    println(output)
                }
            }
        )
    )
}