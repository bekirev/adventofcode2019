package adventofcode2019.day01

import adventofcode2019.linesFromResource
import adventofcode2019.sum
import java.nio.file.Paths
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

private fun getInput() = linesFromResource(
    Paths.get(
        "adventofcode2019",
        "day01",
        "input.txt"
    )
)
    .map(String::toLong)

fun requiredFuelByMass(mass: Long): Long = max(0, mass / 3 - 2)

fun totalRequiredFuelByMass(mass: Long): Long {
    tailrec fun totalRequiredFuelByMass(mass: Long, totalFuel: Long): Long =
        when (val additionalFuel = requiredFuelByMass(mass)) {
            0L -> totalFuel + additionalFuel
            else -> totalRequiredFuelByMass(additionalFuel, totalFuel + additionalFuel)
        }
    return totalRequiredFuelByMass(mass, 0)
}