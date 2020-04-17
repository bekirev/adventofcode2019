package adventofcode2019.grid

interface Grid<T> {
    val bounds: Bounds
    operator fun get(position: Position): T
    fun withElements(redefine: Sequence<Pair<Position, T>>): Grid<T>
}
