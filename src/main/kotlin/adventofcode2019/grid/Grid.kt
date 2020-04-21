package adventofcode2019.grid

interface Grid<T> {
    val bounds: Bounds
    operator fun get(position: Position): T
    fun changeElements(elements: Sequence<Pair<Position, T>>)
}
