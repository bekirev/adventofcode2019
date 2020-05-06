package adventofcode2019.grid

class ArrayGrid<T> private constructor(
    override val bounds: Bounds,
    private val rows: ArrayList<ArrayList<T>>
) : Grid<T> {
    constructor(rows: ArrayList<ArrayList<T>>) : this(
        Bounds(0, rows[0].size - 1, 0, rows.size - 1),
        rows
    )

    private val size by lazy {
        Size(
            bounds.maxX - bounds.minX + 1,
            bounds.maxY - bounds.minY + 1
        )
    }

    init {
        for (i in 0 until size.height) {
            check(rows[i].size == size.width) { "Arrays have different size" }
        }
    }

    companion object {
        fun <T> fromListSequence(lists: Sequence<List<T>>): ArrayGrid<T> {
            return ArrayGrid(ArrayList(lists.map { list -> ArrayList(list) }.toList()))
        }

        fun <T> withBounds(bounds: Bounds, init: (Position) -> T): ArrayGrid<T> {
            val size = bounds.size()
            val rows = ArrayList<ArrayList<T>>(size.height)
            for (rowIndex in 0 until size.height) {
                val y = arrayRowIndexToY(bounds, rowIndex)
                val row = ArrayList<T>(size.width)
                for (x in bounds.xRange) {
                    row.add(init(Position(x, y)))
                }
                rows.add(row)
            }
            return ArrayGrid(bounds, rows)
        }

        private fun xToArrayIndex(bounds: Bounds, x: Int): Int = x - bounds.minX
        private fun yToArrayIndex(bounds: Bounds, y: Int): Int = y - bounds.minY
        private fun arrayRowIndexToY(bounds: Bounds, rowIndex: Int): Int = rowIndex + bounds.minY
    }

    private fun Int.xToArrayIndex(): Int = Companion.xToArrayIndex(bounds, this)
    private fun Int.yToArrayIndex(): Int = Companion.yToArrayIndex(bounds, this)

    override operator fun get(position: Position): T = rows[position.y.yToArrayIndex()][position.x.xToArrayIndex()]

    override fun changeElements(elements: Sequence<Pair<Position, T>>) {
        elements.forEach { (pos, state) -> rows[pos.y.yToArrayIndex()][pos.x.xToArrayIndex()] = state }
    }

    private fun copyOf(): ArrayGrid<T> {
        val newRows = ArrayList<ArrayList<T>>(rows.size)
        for (row in rows) {
            newRows.add(ArrayList(row))
        }
        return ArrayGrid(bounds, newRows)
    }

    fun toString(convertFunction: (T) -> String): String {
        return rows.asSequence()
            .map { row -> row.asSequence().map(convertFunction).joinToString("") }
            .toList()
            .reversed()
            .joinToString(System.lineSeparator())
    }
}
