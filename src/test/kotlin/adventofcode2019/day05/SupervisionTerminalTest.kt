package adventofcode2019.day05

import adventofcode2019.intCodeNumber
import adventofcode2019.intcode.ArrayMemory
import adventofcode2019.intcode.IntCodeNumber
import adventofcode2019.intcode.Memory
import adventofcode2019.intcode.OutputConsumer
import adventofcode2019.intcode.OutputInstruction
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class SupervisionTerminalTest : StringSpec({
    "supervision terminal" {
        val eq8PosMode = listOf(3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8)
        val lt8PosMode = listOf(3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8)
        val eq8ImmMode = listOf(3, 3, 1108, -1, 8, 3, 4, 3, 99)
        val lt8ImmMode = listOf(3, 3, 1107, -1, 8, 3, 4, 3, 99)
        val notEq0PosMode = listOf(3, 12, 6, 12, 15, 1, 13, 14, 13, 4, 13, 99, -1, 0, 1, 9)
        val notEq0ImmMode = listOf(3, 3, 1105, -1, 9, 1101, 0, 0, 12, 4, 12, 99, 1)
        val cmp8 = listOf(
            3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31,
            1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104,
            999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99
        )
        forAll(
            row(eq8PosMode, 0, 0),
            row(eq8PosMode, 7, 0),
            row(eq8PosMode, 8, 1),
            row(eq8PosMode, 9, 0),
            row(eq8PosMode, 13, 0),
            row(lt8PosMode, 1, 1),
            row(lt8PosMode, 6, 1),
            row(lt8PosMode, 7, 1),
            row(lt8PosMode, 8, 0),
            row(lt8PosMode, 9, 0),
            row(eq8ImmMode, 0, 0),
            row(eq8ImmMode, 7, 0),
            row(eq8ImmMode, 8, 1),
            row(eq8ImmMode, 9, 0),
            row(eq8ImmMode, 13, 0),
            row(lt8ImmMode, 1, 1),
            row(lt8ImmMode, 6, 1),
            row(lt8ImmMode, 7, 1),
            row(lt8ImmMode, 8, 0),
            row(lt8ImmMode, 9, 0),
            row(notEq0PosMode, -5, 1),
            row(notEq0PosMode, -1, 1),
            row(notEq0PosMode, 0, 0),
            row(notEq0PosMode, 1, 1),
            row(notEq0PosMode, 7, 1),
            row(notEq0ImmMode, -9, 1),
            row(notEq0ImmMode, -1, 1),
            row(notEq0ImmMode, 0, 0),
            row(notEq0ImmMode, 1, 1),
            row(notEq0ImmMode, 13, 1),
            row(cmp8, 4, 999),
            row(cmp8, 7, 999),
            row(cmp8, 8, 1000),
            row(cmp8, 9, 1001),
            row(cmp8, 17, 1001)

        ) { memoryList, input, expectedOutput ->
            createAndRunIntCode(
                ArrayMemory.of(memoryList.intCodeNumber()),
                input,
                object : OutputConsumer {
                    override suspend fun consume(output: IntCodeNumber) =
                        output shouldBe IntCodeNumber.of(expectedOutput)
                }
            )
        }
    }
})

private fun createAndRunIntCode(memory: Memory, input: Int, outputConsumer: OutputConsumer) {
    createAndRunIntCode(
        memory,
        input,
        OutputInstruction(outputConsumer)
    )
}
