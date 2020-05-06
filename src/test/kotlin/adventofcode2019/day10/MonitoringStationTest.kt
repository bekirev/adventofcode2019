package adventofcode2019.day10

import adventofcode2019.grid.Position
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class MonitoringStationTest : StringSpec({
    "Monitor station test" {
        forAll(
            row(
                """
.#..#
.....
#####
....#
...##""",
                ObservationPosition(Position(3, 4), 8)
            ),
            row(
                """
......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####""",
                ObservationPosition(Position(5, 8), 33)
            ),
            row(
                """
#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###.""",
                ObservationPosition(Position(1, 2), 35)
            ),
            row(
                """
.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#..""",
                ObservationPosition(Position(6, 3), 41)
            ),
            row(
                """
.#..##.###...#######
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
###.##.####.##.#..##""",
                ObservationPosition(Position(11, 13), 210)
            )
        ) { mapStr, obsPos ->
            mapStr.lineSequence().asGrid().bestObservationPosition() shouldBe obsPos
        }
    }
})