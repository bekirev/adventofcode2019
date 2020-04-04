import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

object PathFinder {
    private fun uriFromResources(strPath: String): URI = javaClass.classLoader.getResource(strPath).toURI()

    fun fromResources(strPath: String): Path = Paths.get(uriFromResources(strPath))
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

