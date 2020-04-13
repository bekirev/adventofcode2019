package adventofcode2019.day05

import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.Intcode
import adventofcode2019.intcode.createIntcode
import adventofcode2019.intcode.getInput
import adventofcode2019.intcode.runUntilHalted
import java.nio.file.Paths

fun main() {
    createAndRunIntcode()
}

fun createAndRunIntcode(): Intcode {
    return createIntcode(ArrayMemory.fromSequence(getInput(Paths.get("adventofcode2019", "day05", "input.txt")))).runUntilHalted()
}

