package adventofcode2019.day02

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import org.junit.Assert
import org.junit.Test

class IntCodeTest {
    @Test
    fun test1() {
        val intCode = runIntCode(ArrayMemory.fromIntVararg(1, 0, 0, 0, 99))
        Assert.assertEquals(IntCodeNumber.fromInt(2), intCode.memoryAt(0))
        Assert.assertEquals(IntCodeNumber.fromInt(0), intCode.memoryAt(1))
        Assert.assertEquals(IntCodeNumber.fromInt(0), intCode.memoryAt(2))
        Assert.assertEquals(IntCodeNumber.fromInt(0), intCode.memoryAt(3))
        Assert.assertEquals(IntCodeNumber.fromInt(99), intCode.memoryAt(4))
    }

    @Test
    fun test2() {
        val intCode = runIntCode(ArrayMemory.fromIntVararg(2, 3, 0, 3, 99))
        Assert.assertEquals(IntCodeNumber.fromInt(2), intCode.memoryAt(0))
        Assert.assertEquals(IntCodeNumber.fromInt(3), intCode.memoryAt(1))
        Assert.assertEquals(IntCodeNumber.fromInt(0), intCode.memoryAt(2))
        Assert.assertEquals(IntCodeNumber.fromInt(6), intCode.memoryAt(3))
        Assert.assertEquals(IntCodeNumber.fromInt(99), intCode.memoryAt(4))
    }

    @Test
    fun test3() {
        val intCode = runIntCode(ArrayMemory.fromIntVararg(2, 4, 4, 5, 99, 0))
        Assert.assertEquals(IntCodeNumber.fromInt(2), intCode.memoryAt(0))
        Assert.assertEquals(IntCodeNumber.fromInt(4), intCode.memoryAt(1))
        Assert.assertEquals(IntCodeNumber.fromInt(4), intCode.memoryAt(2))
        Assert.assertEquals(IntCodeNumber.fromInt(5), intCode.memoryAt(3))
        Assert.assertEquals(IntCodeNumber.fromInt(99), intCode.memoryAt(4))
        Assert.assertEquals(IntCodeNumber.fromInt(9801), intCode.memoryAt(5))
    }

    @Test
    fun test4() {
        val intCode = runIntCode(ArrayMemory.fromIntVararg(1, 1, 1, 4, 99, 5, 6, 0, 99))
        Assert.assertEquals(IntCodeNumber.fromInt(30), intCode.memoryAt(0))
        Assert.assertEquals(IntCodeNumber.fromInt(1), intCode.memoryAt(1))
        Assert.assertEquals(IntCodeNumber.fromInt(1), intCode.memoryAt(2))
        Assert.assertEquals(IntCodeNumber.fromInt(4), intCode.memoryAt(3))
        Assert.assertEquals(IntCodeNumber.fromInt(2), intCode.memoryAt(4))
        Assert.assertEquals(IntCodeNumber.fromInt(5), intCode.memoryAt(5))
        Assert.assertEquals(IntCodeNumber.fromInt(6), intCode.memoryAt(6))
        Assert.assertEquals(IntCodeNumber.fromInt(0), intCode.memoryAt(7))
        Assert.assertEquals(IntCodeNumber.fromInt(99), intCode.memoryAt(8))
    }
}