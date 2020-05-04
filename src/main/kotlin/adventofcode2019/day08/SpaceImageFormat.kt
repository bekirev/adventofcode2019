package adventofcode2019.day08

import adventofcode2019.day08.Color.BLACK
import adventofcode2019.day08.Color.TRANSPARENT
import adventofcode2019.day08.Color.WHITE
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.streams.asSequence
import kotlin.streams.asStream

fun main() {
    val size = Size(25, 6)
    val pixelsCount = size.pixelsCount
    val image = Image.fromList(
        size,
        getInput()
            .chunked(pixelsCount)
            .map { chunk: List<Color> -> Layer.fromList(size, chunk) }
            .toList()
    )
    val maxBy = image.layers()
        .map { layer ->
            layer to layer.colorsCount()
        }
        .minBy { it.second[BLACK] ?: 0 }
    println(maxBy?.second?.let { (it[WHITE] ?: 0) * (it[TRANSPARENT] ?: 0) })
    image.decoded.println()
}

private enum class Color {
    BLACK, WHITE, TRANSPARENT
}

private data class Size(val width: Int, val height: Int) {
    val pixelsCount: Int by lazy { width * height }
}

private class Image(private val size: Size, private val layers: Array<Layer>) {
    init {
        layers.forEach { layer ->
            if (size != layer.size) throw IllegalArgumentException("Layer has different size")
        }
    }

    companion object {
        fun fromList(size: Size, list: List<Layer>): Image {
            return Image(size, list.toTypedArray())
        }
    }

    val decoded: Layer by lazy { decode() }

    private fun decode(): Layer {
        fun determineColorAt(position: Position): Color {
            fun reduce(front: Color, back: Color): Color = when (front) {
                BLACK -> BLACK
                WHITE -> WHITE
                TRANSPARENT -> back
            }
            return layers.asSequence().drop(1).fold(layers[0].colorAt(position)) { color: Color, layer: Layer ->
                if (color != TRANSPARENT) return@fold color
                reduce(color, layer.colorAt(position))
            }
        }
        fun Size.allPositions(): Sequence<Position> = sequence {
            for (row in 0 until height) {
                for (column in 0 until width) {
                    yield(Position(row, column))
                }
            }
        }
        return Layer.fromList(
            size,
            size.allPositions()
                .map(::determineColorAt)
                .toList()
        )
    }

    fun layers(): Sequence<Layer> {
        return layers.asSequence()
    }
}

private class Layer(val size: Size, private val data: Array<Color>) {
    companion object {
        internal fun fromList(size: Size, list: List<Color>): Layer = Layer(size, list.toTypedArray())
    }

    fun data(): Sequence<Color> {
        return data.asSequence()
    }

    fun colorAt(position: Position): Color {
        return data[position.row * size.width + position.column]
    }
}

private data class Position(val row: Int, val column: Int)

private fun Layer.println() {
    fun Color.toChar(): Char = when (this) {
        BLACK -> ' '
        WHITE -> '#'
        TRANSPARENT -> '2'
    }

    println(
        data()
            .map(Color::toChar)
            .chunked(size.width)
            .map { line -> line.joinToString("") }
            .joinToString("\n\r")
    )
}

private fun Layer.colorsCount(): Map<Color, Int> {
    return this.data().asStream().collect(
        Collectors.toMap({ it }, { 1 }) { count1, count2 ->
            count1 + count2
        }
    )
}

private fun getInput(): Sequence<Color> {
    fun Char.toColor(): Color = when (this) {
        '0' -> BLACK
        '1' -> WHITE
        '2' -> TRANSPARENT
        else -> throw IllegalArgumentException("Unknown color code $this")
    }

    return Paths.get("adventofcode2019", "day08", "input.txt")
        .linesFromResource()
        .asSequence()
        .flatMap(String::asSequence)
        .map(Char::toColor)
}
