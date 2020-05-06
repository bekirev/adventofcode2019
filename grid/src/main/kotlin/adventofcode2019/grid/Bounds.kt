package adventofcode2019.grid

data class Bounds(
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int
) {
    val xRange by lazy { minX..maxX }
    val yRange by lazy { minY..maxY }

    init {
        check(maxX >= minX) { "maxX shouldn't be less than minX" }
        check(maxY >= minY) { "maxY shouldn't be less than minY" }
    }

    fun allPositions(): Sequence<Position> = sequence {
        for (x in xRange) {
            for (y in yRange) {
                yield(Position(x, y))
            }
        }
    }

    fun size(): Size = Size(maxX - minX + 1, maxY - minY + 1)
}
