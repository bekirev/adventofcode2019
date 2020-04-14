package adventofcode2019.day05

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import org.junit.Assert
import org.junit.Test

class SupervisionTerminal {
    private fun createAndRunIntCode(memory: Memory, input: Int, outputConsumer: OutputConsumer) {
        createAndRunIntCode(
            memory,
            input,
            OutputInstruction(outputConsumer)
        )
    }

    @Test
    fun test1() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            7,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test2() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            8,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test3() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            9,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test4() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            7,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test5() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            8,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test6() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            9,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test7() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            7,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test8() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            8,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test9() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            9,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test10() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            7,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test11() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            8,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test12() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            9,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test13() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9),
            0,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test14() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9),
            2,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test15() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1105,-1,9,1101,0,0,12,4,12,99,1),
            0,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(0, output) }
        )
    }

    @Test
    fun test16() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,3,1105,-1,9,1101,0,0,12,4,12,99,1),
            2,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1, output) }
        )
    }

    @Test
    fun test17() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            7,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(999, output) }
        )
    }

    @Test
    fun test18() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            8,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1000, output) }
        )
    }

    @Test
    fun test19() {
        createAndRunIntCode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            9,
            object : OutputConsumer { override suspend fun consume(output: Int) = Assert.assertEquals(1001, output) }
        )
    }
}