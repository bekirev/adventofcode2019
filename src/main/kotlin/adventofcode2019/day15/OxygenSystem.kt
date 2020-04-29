package adventofcode2019.day15

import adventofcode2019.day15.GridCell.DRONE
import adventofcode2019.day15.GridCell.DRONE_OXYGEN_SYSTEM
import adventofcode2019.day15.GridCell.EXPLORED
import adventofcode2019.day15.GridCell.FOG
import adventofcode2019.day15.GridCell.OXYGEN_SYSTEM
import adventofcode2019.day15.OxygenPropagationCell.EMPTY
import adventofcode2019.day15.OxygenPropagationCell.OXYGEN
import adventofcode2019.day15.OxygenPropagationCell.WALL
import adventofcode2019.grid.ArrayGrid
import adventofcode2019.grid.Grid
import adventofcode2019.grid.Position
import adventofcode2019.intcode.AdditionalMapMemory
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.intCodeInput
import java.nio.file.Paths

fun main() {
    val (map, movesToOxygenSystem) = RepairDroid.fromMemory(
        AdditionalMapMemory.fromMemory(
            ArrayMemory.fromSequence(
                Paths.get("adventofcode2019", "day15", "input.txt")
                    .intCodeInput()
            )
        )
    ).buildMap()
    println(movesToOxygenSystem)
    println(timeToFill(map))
}

private fun timeToFill(map: Grid<GridCell>): Int {
    tailrec fun timeToFill(oxygenMap: Grid<OxygenPropagationCell>, newOxygenPositions: Set<Position>, time: Int): Int {
        fun positionsToGoTo(pos: Position): Sequence<Position> {
            return Direction.values().asSequence()
                .map { direction -> pos + direction }
                .filter { oxygenMap[it] == EMPTY }
        }
        return when {
            newOxygenPositions.isEmpty() -> time - 1
            else -> {
                val newPositions = newOxygenPositions.asSequence().flatMap(::positionsToGoTo).toSet()
                oxygenMap.changeElements(newPositions.asSequence().map { it to OXYGEN })
                timeToFill(
                    oxygenMap,
                    newPositions,
                    time + 1
                )
            }
        }
    }

    val oxygenMap: Grid<OxygenPropagationCell> = ArrayGrid.withBounds(map.bounds) {
        EMPTY
    }.apply {
        changeElements(
            bounds.allPositions()
                .map { pos ->
                    pos to when (map[pos]) {
                        FOG -> WALL
                        DRONE -> EMPTY
                        DRONE_OXYGEN_SYSTEM -> OXYGEN
                        GridCell.WALL -> WALL
                        EXPLORED -> EMPTY
                        OXYGEN_SYSTEM -> OXYGEN
                    }
                }
        )
    }
    return timeToFill(
        oxygenMap,
        setOf(map.bounds.allPositions().first { pos -> oxygenMap[pos] == OXYGEN }),
        0
    )
}

private enum class OxygenPropagationCell {
    EMPTY, WALL, OXYGEN
}