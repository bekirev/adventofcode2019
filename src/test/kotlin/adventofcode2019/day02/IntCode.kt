package adventofcode2019.day02

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.runUntilHalted
import org.junit.Assert
import org.junit.Test

class IntCode {
    @Test
    fun test1() {
        val intcode = createIntcode(ArrayMemory.fromVararg(1, 0, 0, 0, 99)).runUntilHalted()
        Assert.assertEquals(2, intcode.memoryAt(0))
        Assert.assertEquals(0, intcode.memoryAt(1))
        Assert.assertEquals(0, intcode.memoryAt(2))
        Assert.assertEquals(0, intcode.memoryAt(3))
        Assert.assertEquals(99, intcode.memoryAt(4))
    }

    @Test
    fun test2() {
        val intcode = createIntcode(ArrayMemory.fromVararg(2, 3, 0, 3, 99)).runUntilHalted()
        Assert.assertEquals(2, intcode.memoryAt(0))
        Assert.assertEquals(3, intcode.memoryAt(1))
        Assert.assertEquals(0, intcode.memoryAt(2))
        Assert.assertEquals(6, intcode.memoryAt(3))
        Assert.assertEquals(99, intcode.memoryAt(4))
    }

    @Test
    fun test3() {
        val intcode = createIntcode(ArrayMemory.fromVararg(2, 4, 4, 5, 99, 0)).runUntilHalted()
        Assert.assertEquals(2, intcode.memoryAt(0))
        Assert.assertEquals(4, intcode.memoryAt(1))
        Assert.assertEquals(4, intcode.memoryAt(2))
        Assert.assertEquals(5, intcode.memoryAt(3))
        Assert.assertEquals(99, intcode.memoryAt(4))
        Assert.assertEquals(9801, intcode.memoryAt(5))
    }

    @Test
    fun test4() {
        val intcode = createIntcode(ArrayMemory.fromVararg(1, 1, 1, 4, 99, 5, 6, 0, 99)).runUntilHalted()
        Assert.assertEquals(30, intcode.memoryAt(0))
        Assert.assertEquals(1, intcode.memoryAt(1))
        Assert.assertEquals(1, intcode.memoryAt(2))
        Assert.assertEquals(4, intcode.memoryAt(3))
        Assert.assertEquals(2, intcode.memoryAt(4))
        Assert.assertEquals(5, intcode.memoryAt(5))
        Assert.assertEquals(6, intcode.memoryAt(6))
        Assert.assertEquals(0, intcode.memoryAt(7))
        Assert.assertEquals(99, intcode.memoryAt(8))
    }
}