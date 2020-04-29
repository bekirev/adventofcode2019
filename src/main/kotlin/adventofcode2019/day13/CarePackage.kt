package adventofcode2019.day13

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.intCodeInput
import adventofcode2019.linesFromResource
import java.nio.file.Paths
import kotlin.streams.asSequence

@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() {
    day1()
    day2()
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
private fun day1() {
    val gameOnGrid = GameOnGrid.fromMemory(
        ArrayMemory.fromSequence(
            Paths.get("adventofcode2019", "day13", "input.txt")
                .intCodeInput()
        )
    )
    gameOnGrid.run()
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
private fun day2() {
    val gameOnGrid = GameOnGrid.fromMemory(
        ArrayMemory.fromSequence(
            Paths.get("adventofcode2019", "day13", "input.txt")
                .intCodeInput()
        ).apply {
            this[0] = IntCodeNumber.fromInt(2)
        },
        Paths.get("adventofcode2019", "day13", "steps.txt")
            .linesFromResource()
            .asSequence()
            .flatMap { line ->
                line.split(",").asSequence()
                    .filter(String::isNotBlank)
                    .map(String::trim)
                    .map(String::toInt)
            }
            .toList()
    )
    gameOnGrid.run()
}
