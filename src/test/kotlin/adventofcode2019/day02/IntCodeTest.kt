package adventofcode2019.day02

import adventofcode2019.intCodeNumber
import adventofcode2019.intcode.ArrayMemory
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class IntCodeKotest : StringSpec({
    "intcode memory[0] = 1 + 1; memory[0] = 2 * 3, memory[5] = 99 * 99, memory[4] = 1 + 1, memory[0] = 5 * 6" {
        forAll(
            row(
                listOf(1, 0, 0, 0, 99).intCodeNumber(),
                listOf(2, 0, 0, 0, 99).intCodeNumber()
            ),
            row(
                listOf(2, 3, 0, 3, 99).intCodeNumber(),
                listOf(2, 3, 0, 6, 99).intCodeNumber()
            ),
            row(
                listOf(2, 4, 4, 5, 99, 0).intCodeNumber(),
                listOf(2, 4, 4, 5, 99, 9801).intCodeNumber()
            ),
            row(
                listOf(1, 1, 1, 4, 99, 5, 6, 0, 99).intCodeNumber(),
                listOf(30, 1, 1, 4, 2, 5, 6, 0, 99).intCodeNumber()
            )
        ) { startMemory, finishMemory ->
            val intCode = runIntCode(ArrayMemory.of(startMemory))
            finishMemory.forEachIndexed { index, value ->
                intCode.memoryAt(index) shouldBe value
            }
        }
    }
})