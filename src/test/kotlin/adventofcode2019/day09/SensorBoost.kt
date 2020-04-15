package adventofcode2019.day09

import adventofcode2019.digitsCount
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.ReadLineInputInstruction
import adventofcode2019.intcode.createIntCodeAllInstr
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.LinkedList

class SensorBoostTest {
    @Test
    fun test1() {
        val program = sequenceOf(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99)
            .map { IntCodeNumber.fromInt(it) }
            .toList()
        val outputList: MutableList<IntCodeNumber> = LinkedList<IntCodeNumber>()
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.fromList(program)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        outputList.add(output)
                    }
                }
            )
        )
        runBlocking {
            intCode.run()
        }
        Assert.assertEquals(program, outputList)
    }

    @Test
    fun test2() {
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.fromIntVararg(1102,34915192,34915192,7,4,7,99,0)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        Assert.assertEquals(16, output.toLong().digitsCount())
                    }
                }
            )
        )
        runBlocking {
            intCode.run()
        }
    }

    @Test
    fun test3() {
        val outputList: MutableList<IntCodeNumber> = LinkedList<IntCodeNumber>()
        val intCode = createIntCodeAllInstr(
            AdditionalMapMemory.fromMemory(ArrayMemory.fromLongVararg(104,1125899906842624,99)),
            ReadLineInputInstruction,
            OutputInstruction(
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) {
                        outputList.add(output)
                    }
                }
            )
        )
        runBlocking {
            intCode.run()
        }
        Assert.assertEquals(listOf(IntCodeNumber.fromLong(1125899906842624)), outputList)
    }
}