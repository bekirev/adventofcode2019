package adventofcode2019.day01

import adventofcode2019.linesFromResource
import adventofcode2019.sum
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.math.max

fun main() {
    println(
        getInput()
            .map(::requiredFuelByMass)
            .sum()
    )

    println(
        getInput()
            .map(::totalRequiredFuelByMass)
            .sum()
    )
}

private fun getInput(): Stream<Long> {
    return Paths.get("adventofcode2019", "day01", "input.txt")
        .linesFromResource()
        .map(String::toLong)
}

private fun requiredFuelByMass(mass: Long): Long = max(0, mass / 3 - 2)

private fun totalRequiredFuelByMass(mass: Long): Long {
    tailrec fun totalRequiredFuelByMass(mass: Long, totalFuel: Long): Long =
        when (val additionalFuel = requiredFuelByMass(mass)) {
            0L -> totalFuel + additionalFuel
            else -> totalRequiredFuelByMass(additionalFuel, totalFuel + additionalFuel)
        }
    return totalRequiredFuelByMass(mass, 0)
}