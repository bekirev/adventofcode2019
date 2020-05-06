package adventofcode2019.day14

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class SpaceStoichiometryTest : StringSpec({
    "should calculate required chemicals" {
        val a = Chemical("A")
        val b = Chemical("B")
        val c = Chemical("C")
        val ab = Chemical("AB")
        val bc = Chemical("BC")
        val ca = Chemical("CA")
        val fuel = Chemical("FUEL")
        val nanoFactory = NanoFactory.fromSequence(
            sequenceOf(
                +(9 * ORE) produce 2 * a,
                +(8 * ORE) produce 3 * b,
                +(7 * ORE) produce 5 * c,
                3 * a + 4 * b produce +ab,
                5 * b + 7 * c produce +bc,
                4 * c + 1 * a produce +ca,
                2 * ab + 3 * bc + 4 * ca produce +fuel
            )
        )
        nanoFactory.chemicalsToProduce(fuel, BigInteger.valueOf(1)) shouldBe mapOf(ORE to BigInteger.valueOf(165))
    }
})