package adventofcode2019.day07

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import org.junit.Assert
import org.junit.Test

class AmplificationCircuit {
    @Test
    fun test0() {
        Assert.assertEquals(
            IntCodeNumber.fromInt(43210),
            runAmplificationCircuit(
                ArrayMemory.fromIntVararg(3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0),
                PhaseSettings(4, 3, 2, 1, 0)
            )
        )
    }

    @Test
    fun test1() {
        Assert.assertEquals(
            ThrusterSignalResult(
                IntCodeNumber.fromInt(43210),
                PhaseSettings(4,3,2,1,0)
            ),
            findMaxThrusterSignal(
                ArrayMemory.fromIntVararg(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
            )
        )
    }

    @Test
    fun test2() {
        Assert.assertEquals(
            ThrusterSignalResult(
                IntCodeNumber.fromInt(54321),
                PhaseSettings(0,1,2,3,4)
            ),
            findMaxThrusterSignal(
                ArrayMemory.fromIntVararg(3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0)
            )
        )
    }

    @Test
    fun test3() {
        Assert.assertEquals(
            ThrusterSignalResult(
                IntCodeNumber.fromInt(65210),
                PhaseSettings(1,0,4,3,2)
            ),
            findMaxThrusterSignal(
                ArrayMemory.fromIntVararg(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)
            )
        )
    }

    @Test
    fun test4() {
        Assert.assertEquals(
            ThrusterSignalResult(
                IntCodeNumber.fromInt(139629729),
                PhaseSettings(9,8,7,6,5)
            ),
            findMaxThrusterSignalWithFeedback(
                ArrayMemory.fromIntVararg(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)
            )
        )
    }
}