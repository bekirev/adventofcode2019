package adventofcode2019.day10

import adventofcode2019.day10.PositionState.ASTEROID
import adventofcode2019.day10.PositionState.EMPTY
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.streams.asSequence

fun main() {
    fun getInput(): AsteroidMap {
        return linesFromResource(Paths.get("adventofcode2019", "day10", "input.txt"))
            .asSequence()
            .input()
    }
    val asteroidMap = getInput()
    val observationPosition = asteroidMap.bestObservationPosition()
    println(observationPosition)
    val asteroidNumber200 = asteroidMap.laserVaporizationSequence(observationPosition.position).drop(199).first()
    println("$asteroidNumber200: ${asteroidNumber200.position.x * 100 + asteroidNumber200.position.y}")
}

class AsteroidMap private constructor(private val rows: Array<Array<PositionState>>) {
    val size: Size = Size(rows[0].size, rows.size)

    init {
        for (i in 1 until size.height) {
            if (rows[i].size != size.width) throw IllegalStateException("Arrays have different size")
        }
    }

    companion object {
        fun fromSequence(arrays: Sequence<Array<PositionState>>): AsteroidMap =
            AsteroidMap(arrays.toList().toTypedArray())
    }

    operator fun get(position: Position): PositionState = rows[position.y][position.x]

    fun withEmptyPositions(positions: Sequence<Position>): AsteroidMap {
        val newRows = Array(rows.size) { row ->
            rows[row].copyOf()
        }
        positions.forEach { (x, y) -> newRows[y][x] = EMPTY }
        return AsteroidMap(newRows)
    }

    override fun toString(): String {
        fun PositionState.toChar(): Char = when (this) {
            EMPTY -> '#'
            ASTEROID -> '.'
        }
        return rows.asSequence()
            .map { row -> row.asSequence().map(PositionState::toChar).joinToString("") }
            .joinToString(System.lineSeparator())
    }
}

fun AsteroidMap.bestObservationPosition(): ObservationPosition {
    fun observationPossibilities(position: Position): Int {
        return visibleAsteroids(this, position)
            .count()
    }
    val (pos, count) = size.allPositions()
        .filter { this[it] == ASTEROID }
        .map {
            it to observationPossibilities(it)
        }
        .maxBy { it.second }!!
    return ObservationPosition(pos, count)
}

data class ObservationPosition(val position: Position, val visibleAsteroidsCount: Int)

private fun AsteroidMap.laserVaporizationSequence(position: Position): Sequence<VisibleAsteroid> = sequence {
    var asteroidMap: AsteroidMap = this@laserVaporizationSequence
    var asteroids: List<VisibleAsteroid> = emptyList()
    val iteration = {
        asteroids = visibleAsteroids(asteroidMap, position).sortedBy(VisibleAsteroid::angle).toList()
        asteroidMap = asteroidMap.withEmptyPositions(asteroids.asSequence().map(VisibleAsteroid::position))
    }
    do {
        iteration()
        yieldAll(asteroids)
    } while (asteroids.isNotEmpty())
}

private fun visibleAsteroids(asteroidMap: AsteroidMap, position: Position): Sequence<VisibleAsteroid> {
    fun possibleAnglesFromPosition(size: Size, position: Position): Set<Angle> {
        tailrec fun gcd(a: Int, b: Int): Int = when (b) {
            0 -> a
            else -> gcd(b, a % b)
        }
        return size.allPositions()
            .map { it - position }
            .filter { relativePosition -> relativePosition != Position.ZERO }
            .map { relativePosition ->
                val gcd = gcd(relativePosition.x.absoluteValue, relativePosition.y.absoluteValue)
                Angle(relativePosition.x / gcd, relativePosition.y / gcd)
            }
            .toSet()
    }
    fun firstVisibleAsteroid(angle: Angle): VisibleAsteroid? {
        operator fun Position.plus(angle: Angle): Position {
            return Position(x + angle.x, y + angle.y)
        }
        fun Position.inside(size: Size): Boolean = x < size.width && x >= 0 && y < size.height && y >= 0
        val positions = sequence<Position> {
            var nextPosition = position + angle
            while (nextPosition.inside(asteroidMap.size)) {
                yield(nextPosition)
                nextPosition += angle
            }
        }
        return positions.asSequence()
            .firstOrNull { pos -> asteroidMap[pos] == ASTEROID }
            ?.let { pos -> VisibleAsteroid(pos, angle) }
    }
    return possibleAnglesFromPosition(asteroidMap.size, position)
        .asSequence()
        .map(::firstVisibleAsteroid)
        .filterNotNull()
}

private data class VisibleAsteroid(val position: Position, val angle: Angle)

data class Size(val width: Int, val height: Int)

private fun Size.allPositions(): Sequence<Position> = sequence {
    for (x in 0 until width) {
        for (y in 0 until height) {
            yield(Position(x, y))
        }
    }
}

data class Position(val x: Int, val y: Int) {
    companion object {
        val ZERO = Position(0, 0)
    }

    operator fun minus(other: Position): Position = Position(x - other.x, y - other.y)
}

enum class PositionState {
    EMPTY, ASTEROID
}

private data class Angle(val x: Int, val y: Int) : Comparable<Angle> {
    val radian: Double by lazy { normalised.radian }
    val normalised: Vector by lazy { Vector(x.toDouble(), y.toDouble()).normalised }

    companion object {
        private data class Vector(val x: Double, val y: Double) {
            val radian: Double by lazy {
                val epsilon = 0.000001
                when {
                    x.absoluteValue < epsilon -> if (y > 0) 0.0 else -PI
                    x >= epsilon -> asin(y) - PI / 2
                    else -> PI / 2 - asin(y)
                }
            }
            val normalised: Vector by lazy {
                val length = sqrt(x.pow(2) + y.pow(2))
                Vector(x / length, y / length)
            }
        }
    }

    override fun compareTo(other: Angle): Int = radian.compareTo(other.radian)
}

internal fun Sequence<String>.input(): AsteroidMap {
    fun Char.toPositionState(): PositionState = when (this) {
        '.' -> EMPTY
        '#' -> ASTEROID
        else -> throw IllegalArgumentException("Unknown char for position state: $this")
    }
    return AsteroidMap.fromSequence(
        this.map(String::asSequence)
            .map { it.map(Char::toPositionState).toList().toTypedArray() }
    )
}
