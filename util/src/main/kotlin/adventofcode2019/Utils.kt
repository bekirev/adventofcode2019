package adventofcode2019

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.math.absoluteValue
import kotlin.math.log10

object PathFinder {
    private fun uriFromResources(strPath: String): URI {
        return javaClass.classLoader.getResource(strPath)?.toURI() ?: throw error("Resource not found: $strPath")
    }

    fun fromResources(strPath: String): Path {
        return Paths.get(
            uriFromResources(
                strPath
            )
        )
    }
}

private fun String.linesFromResource(): Stream<String> = Files.lines(PathFinder.fromResources(this))!!

fun Path.linesFromResource(): Stream<String> = this.toString().linesFromResource()

fun Stream<Long>.sum(): Long {
    return reduce(
        0L,
        { acc, value -> acc + value },
        { acc1, acc2 -> acc1 + acc2 }
    ) ?: 0L
}

fun Stream<String>.concat(): String {
    return reduce(
        StringBuilder(),
        { acc, value -> acc.append(value) },
        { acc1, acc2 -> acc1.append(acc2) }
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
