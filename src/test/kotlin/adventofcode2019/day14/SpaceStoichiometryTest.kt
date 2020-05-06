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
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(ORE, BigInteger.valueOf(9))
                        )
                    ),
                    ChemicalReactionItem(a, BigInteger.valueOf(2))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(ORE, BigInteger.valueOf(8))
                        )
                    ),
                    ChemicalReactionItem(b, BigInteger.valueOf(3))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(ORE, BigInteger.valueOf(7))
                        )
                    ),
                    ChemicalReactionItem(c, BigInteger.valueOf(5))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(a, BigInteger.valueOf(3)),
                            ChemicalReactionItem(b, BigInteger.valueOf(4))
                        )
                    ),
                    ChemicalReactionItem(ab, BigInteger.valueOf(1))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(b, BigInteger.valueOf(5)),
                            ChemicalReactionItem(c, BigInteger.valueOf(7))
                        )
                    ),
                    ChemicalReactionItem(bc, BigInteger.valueOf(1))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(c, BigInteger.valueOf(4)),
                            ChemicalReactionItem(a, BigInteger.valueOf(1))
                        )
                    ),
                    ChemicalReactionItem(ca, BigInteger.valueOf(1))
                ),
                ChemicalReaction(
                    InputChemicals(
                        setOf(
                            ChemicalReactionItem(ab, BigInteger.valueOf(2)),
                            ChemicalReactionItem(bc, BigInteger.valueOf(3)),
                            ChemicalReactionItem(ca, BigInteger.valueOf(4))
                        )
                    ),
                    ChemicalReactionItem(fuel, BigInteger.valueOf(1))
                )
            )
        )
        nanoFactory.chemicalsToProduce(fuel, BigInteger.valueOf(1)) shouldBe mapOf(ORE to BigInteger.valueOf(165))
    }
})