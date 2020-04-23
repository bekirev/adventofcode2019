package adventofcode2019

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.math.absoluteValue
import kotlin.math.log10

object PathFinder {
    private fun uriFromResources(strPath: String): URI = javaClass.classLoader.getResource(strPath).toURI()

    fun fromResources(strPath: String): Path = Paths.get(
        uriFromResources(
            strPath
        )
    )
}

fun linesFromResource(strPath: String): Stream<String> =
    Files.lines(PathFinder.fromResources(strPath))!!

fun linesFromResource(path: Path): Stream<String> =
    linesFromResource(path.toString())

fun longLinesStream(inputPath1: Path): Stream<List<Long>> {
    return linesFromResource(inputPath1)
        .map { line ->
            line
                .split("\t")
                .map(String::toLong)
        }
}

fun Stream<Long>.sum(): Long {
    return reduce<Long>(
        0,
        { acc, value -> acc + value },
        { acc1, acc2 -> acc1 + acc2 }
    ) ?: 0
}

fun Stream<String>.concat(): String {
    return reduce<StringBuilder>(
        StringBuilder(),
        {acc, value -> acc.append(value) },
        {acc1, acc2 -> acc1.append(acc2) }
    ).toString()
}

fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
    return c1.flatMap { c1Elem -> c2.map { c2Elem -> c1Elem to c2Elem } }
}

fun Int.pow(n: Int): Int {
    tailrec fun pow(value: Int, n: Int, result: Int): Int {
        return if (n < 1) result else pow(value, n - 1, result * value)
    }
    if (n < 0) throw IllegalArgumentException("Negative power is not supported")
    else return pow(this, n, 1)
}

fun Int.getDigitAt(index: Int): Int {
    return (this % 10.pow(index + 1) - this % 10.pow(index)).absoluteValue / 10.pow(index)
}

fun Int.first(n: Int): Int {
    if (n < 1) throw IllegalArgumentException("Negative power is not supported")
    return (this % 10.pow(n))
}

fun Int.digitsCount(): Int {
    return log10(this.toDouble()).toInt() + 1
}

fun Long.digitsCount(): Int {
    return log10(this.toDouble()).toInt() + 1
}
