package adventofcode2019.day16

import adventofcode2019.getDigitAt
import adventofcode2019.linesFromResource
import adventofcode2019.pow
import com.google.common.collect.Streams
import java.nio.file.Paths
import java.util.Arrays
import java.util.stream.IntStream
import kotlin.streams.asSequence
import kotlin.streams.asStream

fun main() {
    val pattern = arrayOf(0, 1, 0, -1).toIntArray()
    val inputPart1 = linesFromResource(Paths.get("adventofcode2019", "day16", "input.txt"))
        .asSequence()
        .joinToString("")
        .map(Character::getNumericValue)
        .toList()
        .toTypedArray()
        .toIntArray()
    val msgSize = 8
    println(fft(pattern, 100, inputPart1).take(msgSize).joinToString(""))
    val inputPart2 = sequence { for (i in 1..10000) yield(inputPart1.asSequence()) }.flatten().toList().toTypedArray().toIntArray()
    val offset = inputPart2.take(7).reversed().mapIndexed { index, digit -> digit * 10.pow(index + 1) }.sum()
    println(fft(pattern, 100, inputPart2).asSequence().drop(offset).take(msgSize).joinToString(""))
}

private fun patternForPhase(pattern: IntArray, times: Int): Sequence<Int> = sequence {
    while (true) {
        for (p in pattern) {
            for (i in 1..times) {
                yield(p)
            }
        }
    }
}.drop(1)

private fun fft(pattern: IntArray, phasesCount: Int, input: IntArray): IntArray {
    tailrec fun ftt(phase: Int, input: IntArray): IntArray {
        return when {
            phase <= phasesCount -> ftt(
                phase + 1,
                IntStream.rangeClosed(1, input.size)
                    .parallel()
                    .map { index ->
                        Streams.zip(Arrays.stream(input).boxed(), patternForPhase(pattern, index).asStream()) { a, b -> a * b}
                            .reduce(0, { a, b -> a + b }, { a, b -> a + b })
                            .getDigitAt(0)
                    }
                    .toArray()
            )
            else -> input
        }
    }
    return ftt(1, input)
}
