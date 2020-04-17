package adventofcode2019.grid

data class Position(val x: Int, val y: Int) {
    companion object {
        val ZERO = Position(0, 0)
    }

    operator fun minus(other: Position): Position =
        Position(x - other.x, y - other.y)

    operator fun plus(angle: Angle): Position =
        Position(x + angle.x, y + angle.y)
}
