package adventofcode2019.day09

import adventofcode2019.digitsCount
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.ReadLineInputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.LinkedList

class SensorBoostTest : StringSpec({
    "Program should output itself" {
        val program = sequenceOf(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99)
            .map { IntCodeNumber.of(it) }
            .toList()
        val outputList: MutableList<IntCodeNumber> = LinkedList<IntCodeNumber>()
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.of(program)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        outputList.add(output)
                    }
                }
            )
        )
        intCode.run()
        outputList shouldBe program
    }
    "should output a 16-digit number" {
        val outputList: MutableList<IntCodeNumber> = LinkedList<IntCodeNumber>()
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.of(1102, 34915192, 34915192, 7, 4, 7, 99, 0)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        outputList.add(output)
                    }
                }
            )
        )
        intCode.run()
        outputList.size shouldBe 1
        outputList[0].toLong().digitsCount() shouldBe 16
    }
    "should output the large number in the middle" {
        val outputList: MutableList<IntCodeNumber> = LinkedList<IntCodeNumber>()
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.of(104, 1125899906842624, 99)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        outputList.add(output)
                    }
                }
            )
        )
        intCode.run()
        outputList.size shouldBe 1
        outputList[0] shouldBe IntCodeNumber.of(1125899906842624)
    }
})