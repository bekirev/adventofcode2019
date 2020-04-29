package adventofcode2019.day03

import adventofcode2019.cartesianProduct
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.streams.toList

private val ZERO_POINT = Point(0, 0)

fun main() {
    val input = Paths.get("adventofcode2019", "day03", "input.txt")
        .linesFromResource()
        .toList()
    val res = findClosestIntersection(input[0], input[1])
    if (res != null) {
        val (point, distance) = res
        println("Point: $point; distance: $distance")
    } else {
        println("No intersection")
    }
}

fun findClosestIntersection(wirePathsA: String, wirePathsB: String): Pair<Point, Int>? {
    return cartesianProduct(lineToWires(wirePathsA), lineToWires(wirePathsB))
        .asSequence()
        .map { crossing(it.first, it.second) }
        .filterIsInstance<Crossing.PointCrossing>()
        .filter { it.point != ZERO_POINT }
        .map { it -> Pair<Point, Int>(it.point, manhattanDistance(ZERO_POINT, it.point)) }
        .minBy { it.second }
}

private fun lineToWires(line: String): List<Wire> {
    return line.split(",")
        .asSequence()
        .map(String::parseWirePath)
        .fold(ArrayList<Wire>() as MutableList<Wire>) { acc, wirePath ->
            acc.add(
                Wire(
                    if (acc.isEmpty()) ZERO_POINT else acc.last().endPoint,
                    wirePath
                )
            )
            acc
        }
}

private enum class Direction {
    UP, RIGHT, DOWN, LEFT
}

private enum class DirectionAxe {
    HORIZONTAL, VERTICAL
}

private fun Direction.axe(): DirectionAxe = when (this) {
    Direction.UP, Direction.DOWN -> DirectionAxe.VERTICAL
    else -> DirectionAxe.HORIZONTAL
}

private data class WirePath(
    val direction: Direction,
    val length: Int
)

private fun String.parseWirePath(): WirePath {
    return WirePath(
        when (this[0]) {
            'U' -> Direction.UP
            'R' -> Direction.RIGHT
            'D' -> Direction.DOWN
            'L' -> Direction.LEFT
            else -> throw IllegalArgumentException("Unknown direction: ${this[0]}")
        },
        substring(1).toInt()
    )
}

private data class Wire(val startPoint: Point, val wirePath: WirePath) {
    val endPoint: Point by lazy { startPoint.plus(wirePath) }
}

data class Point(val x: Int, val y: Int)

private fun Point.plus(wirePath: WirePath): Point {
    return when (wirePath.direction) {
        Direction.UP -> Point(x, y + wirePath.length)
        Direction.RIGHT -> Point(x + wirePath.length, y)
        Direction.DOWN -> Point(x, y - wirePath.length)
        Direction.LEFT -> Point(x - wirePath.length, y)
    }
}

fun manhattanDistance(pointA: Point, pointB: Point): Int {
    return abs(pointA.x - pointB.x) + abs(pointA.y - pointB.y)
}

private fun crossing(wireA: Wire, wireB: Wire): Crossing {
    fun crossing(crossPoint: Point, horRange: IntRange, verRange: IntRange): Crossing =
        if (horRange.contains(crossPoint.x) && verRange.contains(crossPoint.y)) Crossing.PointCrossing(crossPoint)
        else Crossing.NoCrossing

    return when (wireA.wirePath.direction.axe()) {
        DirectionAxe.HORIZONTAL -> when (wireB.wirePath.direction.axe()) {
            DirectionAxe.HORIZONTAL -> Crossing.NoCrossing
            DirectionAxe.VERTICAL -> crossing(
                Point(wireB.startPoint.x, wireA.startPoint.y),
                wireA.toIntRange(),
                wireB.toIntRange()
            )
        }
        DirectionAxe.VERTICAL -> when (wireB.wirePath.direction.axe()) {
            DirectionAxe.HORIZONTAL -> crossing(
                Point(wireA.startPoint.x, wireB.startPoint.y),
                wireB.toIntRange(),
                wireA.toIntRange()
            )
            DirectionAxe.VERTICAL -> Crossing.NoCrossing
        }
    }
}

private fun Wire.toIntRange(): IntRange {
    return when (wirePath.direction) {
        Direction.UP -> IntRange(startPoint.y, startPoint.y + wirePath.length)
        Direction.RIGHT -> IntRange(startPoint.x, startPoint.x + wirePath.length)
        Direction.DOWN -> IntRange(startPoint.y - wirePath.length, startPoint.y)
        Direction.LEFT -> IntRange(startPoint.x - wirePath.length, startPoint.x)
    }
}

private sealed class Crossing() {
    object NoCrossing : Crossing()
    data class PointCrossing(val point: Point) : Crossing()
}
