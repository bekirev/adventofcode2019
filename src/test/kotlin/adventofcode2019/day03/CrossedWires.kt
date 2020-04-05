package adventofcode2019.day03

import org.junit.Assert.assertEquals
import org.junit.Test

class CrossedWires {
    @Test
    fun testCaseOne() {
        val wirePathsA = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        val wirePathsB = "U62,R66,U55,R34,D71,R55,D58,R83"
        assertEquals(159, findClosestIntersection(wirePathsA, wirePathsB)!!.second);
    }

    @Test
    fun testCaseTwo() {
        val wirePathsA = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        val wirePathsB = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
        assertEquals(135, findClosestIntersection(wirePathsA, wirePathsB)!!.second);
    }
}