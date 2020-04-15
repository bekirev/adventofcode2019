package adventofcode2019.day10

import org.junit.Assert
import org.junit.Test

class MonitoringStationTest {
    @Test
    fun test0() {
        val map = """.#..#
.....
#####
....#
...##""".lineSequence().input()
        Assert.assertEquals(ObservationPosition(Position(3,4), 8), map.bestObservationPosition())
    }

    @Test
    fun test1() {
        val map = """......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####""".lineSequence().input()
        Assert.assertEquals(ObservationPosition(Position(5,8), 33), map.bestObservationPosition())
    }

    @Test
    fun test2() {
        val map = """#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###.""".lineSequence().input()
        Assert.assertEquals(ObservationPosition(Position(1,2), 35), map.bestObservationPosition())
    }

    @Test
    fun test3() {
        val map = """.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#..""".lineSequence().input()
        Assert.assertEquals(ObservationPosition(Position(6,3), 41), map.bestObservationPosition())
    }

    @Test
    fun test4() {
        val map = """.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##""".lineSequence().input()
        Assert.assertEquals(ObservationPosition(Position(11,13), 210), map.bestObservationPosition())
    }

    @Test
    fun test5() {

    }
}