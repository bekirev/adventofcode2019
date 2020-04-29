package adventofcode2019.day10

import adventofcode2019.day10.PositionState.ASTEROID
import adventofcode2019.day10.PositionState.EMPTY
import adventofcode2019.grid.Angle
import adventofcode2019.grid.ArrayGrid
import adventofcode2019.grid.Bounds
import adventofcode2019.grid.Grid
import adventofcode2019.grid.Position
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.math.absoluteValue
import kotlin.streams.asSequence

fun main() {
    fun getInput(): Grid<PositionState> {
        return Paths.get("adventofcode2019", "day10", "input.txt")
            .linesFromResource()
            .asSequence()
            .asGrid()
    }
    val asteroidMap = getInput()
    val observationPosition = asteroidMap.bestObservationPosition()
    println(observationPosition)
    val asteroidNumber200 = asteroidMap.laserVaporizationSequence(observationPosition.position).drop(199).first()
    println("$asteroidNumber200: ${asteroidNumber200.position.x * 100 + asteroidNumber200.position.y}")
}

fun Grid<PositionState>.bestObservationPosition(): ObservationPosition {
    fun observationPossibilities(position: Position): Int {
        return visibleAsteroids(this, position)
            .count()
    }
    val (pos, count) = bounds.allPositions()
        .filter { this[it] == ASTEROID }
        .map {
            it to observationPossibilities(it)
        }
        .maxBy { it.second }!!
    return ObservationPosition(pos, count)
}

data class ObservationPosition(val position: Position, val visibleAsteroidsCount: Int)

private fun Grid<PositionState>.laserVaporizationSequence(position: Position): Sequence<VisibleAsteroid> = sequence {
    val asteroidMap: Grid<PositionState> = this@laserVaporizationSequence
    var asteroids: List<VisibleAsteroid> = emptyList()
    val iteration = {
        asteroids = visibleAsteroids(asteroidMap, position).sortedBy(VisibleAsteroid::angle).toList()
        asteroidMap.changeElements(
            asteroids.asSequence()
                .map(VisibleAsteroid::position)
                .map { pos -> pos to EMPTY }
        )
    }
    do {
        iteration()
        yieldAll(asteroids)
    } while (asteroids.isNotEmpty())
}

private fun visibleAsteroids(asteroidMap: Grid<PositionState>, position: Position): Sequence<VisibleAsteroid> {
    fun possibleAnglesFromPosition(bounds: Bounds, position: Position): Set<Angle> {
        tailrec fun gcd(a: Int, b: Int): Int = when (b) {
            0 -> a
            else -> gcd(b, a % b)
        }
        return bounds.allPositions()
            .map { it - position }
            .filter { relativePosition -> relativePosition != Position.ZERO }
            .map { relativePosition ->
                val gcd = gcd(relativePosition.x.absoluteValue, relativePosition.y.absoluteValue)
                Angle(relativePosition.x / gcd, relativePosition.y / gcd)
            }
            .toSet()
    }
    fun firstVisibleAsteroid(angle: Angle): VisibleAsteroid? {
        fun Position.inside(bounds: Bounds): Boolean = x in bounds.xRange && y in bounds.yRange
        val positions = sequence {
            var nextPosition = position + angle
            while (nextPosition.inside(asteroidMap.bounds)) {
                yield(nextPosition)
                nextPosition += angle
            }
        }
        return positions.asSequence()
            .firstOrNull { pos -> asteroidMap[pos] == ASTEROID }
            ?.let { pos -> VisibleAsteroid(pos, angle) }
    }
    return possibleAnglesFromPosition(asteroidMap.bounds, position)
        .asSequence()
        .map(::firstVisibleAsteroid)
        .filterNotNull()
}

private data class VisibleAsteroid(val position: Position, val angle: Angle)

enum class PositionState {
    EMPTY, ASTEROID
}

internal fun Sequence<String>.asGrid(): Grid<PositionState> {
    fun Char.toPositionState(): PositionState = when (this) {
        '.' -> EMPTY
        '#' -> ASTEROID
        else -> throw IllegalArgumentException("Unknown char for position state: $this")
    }
    return ArrayGrid.fromListSequence(
        this.map(String::asSequence)
            .map { it.map(Char::toPositionState).toList() }
    )
}
