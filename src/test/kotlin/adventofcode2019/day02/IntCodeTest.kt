package adventofcode2019.day02

import adventofcode2019.intcode.ArrayMemory
import org.junit.Assert
import org.junit.Test

class IntCodeTest {
    @Test
    fun test1() {
        val intCode = runIntCode(ArrayMemory.fromVararg(1, 0, 0, 0, 99))
        Assert.assertEquals(2, intCode.memoryAt(0))
        Assert.assertEquals(0, intCode.memoryAt(1))
        Assert.assertEquals(0, intCode.memoryAt(2))
        Assert.assertEquals(0, intCode.memoryAt(3))
        Assert.assertEquals(99, intCode.memoryAt(4))
    }

    @Test
    fun test2() {
        val intCode = runIntCode(ArrayMemory.fromVararg(2, 3, 0, 3, 99))
        Assert.assertEquals(2, intCode.memoryAt(0))
        Assert.assertEquals(3, intCode.memoryAt(1))
        Assert.assertEquals(0, intCode.memoryAt(2))
        Assert.assertEquals(6, intCode.memoryAt(3))
        Assert.assertEquals(99, intCode.memoryAt(4))
    }

    @Test
    fun test3() {
        val intCode = runIntCode(ArrayMemory.fromVararg(2, 4, 4, 5, 99, 0))
        Assert.assertEquals(2, intCode.memoryAt(0))
        Assert.assertEquals(4, intCode.memoryAt(1))
        Assert.assertEquals(4, intCode.memoryAt(2))
        Assert.assertEquals(5, intCode.memoryAt(3))
        Assert.assertEquals(99, intCode.memoryAt(4))
        Assert.assertEquals(9801, intCode.memoryAt(5))
    }

    @Test
    fun test4() {
        val intCode = runIntCode(ArrayMemory.fromVararg(1, 1, 1, 4, 99, 5, 6, 0, 99))
        Assert.assertEquals(30, intCode.memoryAt(0))
        Assert.assertEquals(1, intCode.memoryAt(1))
        Assert.assertEquals(1, intCode.memoryAt(2))
        Assert.assertEquals(4, intCode.memoryAt(3))
        Assert.assertEquals(2, intCode.memoryAt(4))
        Assert.assertEquals(5, intCode.memoryAt(5))
        Assert.assertEquals(6, intCode.memoryAt(6))
        Assert.assertEquals(0, intCode.memoryAt(7))
        Assert.assertEquals(99, intCode.memoryAt(8))
    }
}