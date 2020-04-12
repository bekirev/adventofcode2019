package adventofcode2019.day05

import adventofcode2019.intcode.AddInstruction
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.Code
import adventofcode2019.intcode.Equals
import adventofcode2019.intcode.HaltInstruction
import adventofcode2019.intcode.HybridCommandReader
import adventofcode2019.intcode.InputInstruction
import adventofcode2019.intcode.Intcode
import adventofcode2019.intcode.JumpIfFalse
import adventofcode2019.intcode.JumpIfTrue
import adventofcode2019.intcode.LessThan
import adventofcode2019.intcode.MapInstructionReader
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.MultiplyInstruction
import adventofcode2019.intcode.OutputInstruction
import adventofcode2019.intcode.runUntilHalted
import org.junit.Assert
import org.junit.Test

class SupervisionTerminal {
    @Test
    fun test1() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            7
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test2() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            8
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test3() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,8,9,10,9,4,9,99,-1,8),
            9
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test4() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            7
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test5() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            8
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test6() {
        createIntcode(
            ArrayMemory.fromVararg(3,9,7,9,10,9,4,9,99,-1,8),
            9
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test7() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            7
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test8() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            8
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test9() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1108,-1,8,3,4,3,99),
            9
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test10() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            7
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test11() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            8
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test12() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1107,-1,8,3,4,3,99),
            9
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test13() {
        createIntcode(
            ArrayMemory.fromVararg(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9),
            0
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test14() {
        createIntcode(
            ArrayMemory.fromVararg(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9),
            2
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test15() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1105,-1,9,1101,0,0,12,4,12,99,1),
            0
        ) { output ->
            Assert.assertEquals(0, output)
        }.runUntilHalted()
    }

    @Test
    fun test16() {
        createIntcode(
            ArrayMemory.fromVararg(3,3,1105,-1,9,1101,0,0,12,4,12,99,1),
            2
        ) { output ->
            Assert.assertEquals(1, output)
        }.runUntilHalted()
    }

    @Test
    fun test17() {
        createIntcode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            7
        ) { output ->
            Assert.assertEquals(999, output)
        }.runUntilHalted()
    }

    @Test
    fun test18() {
        createIntcode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            8
        ) { output ->
            Assert.assertEquals(1000, output)
        }.runUntilHalted()
    }

    @Test
    fun test19() {
        createIntcode(
            ArrayMemory.fromVararg(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99),
            9
        ) { output ->
            Assert.assertEquals(1001, output)
        }.runUntilHalted()
    }


    fun createIntcode(memory: Memory, input: Int, outputConsumer: (Int) -> Unit): Intcode {
        return Intcode(
            memory,
            HybridCommandReader(
                MapInstructionReader(
                    mapOf(
                        Code(1) to AddInstruction,
                        Code(2) to MultiplyInstruction,
                        Code(3) to InputInstruction { input },
                        Code(4) to OutputInstruction(outputConsumer),
                        Code(5) to JumpIfTrue,
                        Code(6) to JumpIfFalse,
                        Code(7) to LessThan,
                        Code(8) to Equals,
                        Code(99) to HaltInstruction
                    )
                )
            )
        )
    }
}