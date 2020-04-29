package adventofcode2019.day14

import adventofcode2019.linesFromResource
import java.math.BigInteger
import java.nio.file.Paths
import kotlin.streams.asSequence

fun main() {
    val factory = NanoFactory.fromSequence(
        Paths.get("adventofcode2019", "day14", "input.txt")
            .linesFromResource()
            .asSequence()
            .map(String::toReaction)
    )
    val fuel = Chemical("FUEL")
    val chemicals = factory.chemicalsToProduce(fuel, BigInteger.ONE)
    val oreQuantity = chemicals[ORE] ?: error("No ore")
    println(oreQuantity)
    val maxOreQuantity = BigInteger.valueOf(1_000_000_000_000)
    println(
        searchMaxArg(maxOreQuantity / oreQuantity, maxOreQuantity) { fuelQuantity ->
            factory.chemicalsToProduce(fuel, fuelQuantity)[ORE] ?: error("No ore")
        }
    )
}

fun searchMaxArg(baseArg: BigInteger, maxValue: BigInteger, function: (BigInteger) -> BigInteger): BigInteger {
    tailrec fun findRange(baseArg: BigInteger, baseValue: BigInteger): Pair<BigInteger, BigInteger> {
        return if (baseValue < maxValue) {
            val rightArg = baseArg * BigInteger.TWO
            val rightValue = function(rightArg)
            if (rightValue <= maxValue) {
                findRange(rightArg, rightValue)
            } else {
                baseArg to rightArg
            }
        } else {
            val leftArg = baseArg / BigInteger.TWO
            val leftValue = function(leftArg)
            if (leftValue > maxValue) {
                findRange(leftArg, leftValue)
            } else {
                leftArg to baseArg
            }
        }
    }

    tailrec fun findMaxArg(leftArg: BigInteger, rightArg: BigInteger): BigInteger {
        return if (rightArg - leftArg <= BigInteger.TWO) {
            if (function(rightArg) == maxValue) {
                rightArg
            } else {
                leftArg
            }
        } else {
            val middleArg = leftArg + (rightArg - leftArg) / BigInteger.TWO
            val middleValue = function(middleArg)
            if (middleValue <= maxValue) {
                findMaxArg(middleArg, rightArg)
            } else {
                findMaxArg(leftArg, middleArg)
            }
        }
    }

    val range = findRange(baseArg, function(baseArg))
    return findMaxArg(range.first, range.second)
}

fun String.toReaction(): ChemicalReaction {
    fun String.toChemicalReactionItem(): ChemicalReactionItem {
        val split = split(" ")
        return ChemicalReactionItem(
            Chemical(split[1]),
            split[0].toBigInteger()
        )
    }

    fun String.toInputChemicals(): InputChemicals {
        return InputChemicals(
            split(",")
                .asSequence()
                .map(String::trim)
                .map(String::toChemicalReactionItem)
                .toSet()
        )
    }

    val split = split("=>")
    return ChemicalReaction(
        split[0].trim().toInputChemicals(),
        split[1].trim().toChemicalReactionItem()
    )
}

class NanoFactory private constructor(private val reactions: Map<Chemical, ChemicalReaction>) {
    companion object {
        private val RAW_MATERIALS = setOf(ORE)
        fun fromSequence(sequence: Sequence<ChemicalReaction>): NanoFactory {
            val map = mutableMapOf<Chemical, ChemicalReaction>()
            for (reaction in sequence) {
                if (map[reaction.outputChemical.chemical] == null) {
                    map[reaction.outputChemical.chemical] = reaction
                } else {
                    throw IllegalStateException("Duplicated chemical output")
                }
            }
            return NanoFactory(map)
        }
    }

    fun chemicalsToProduce(chemical: Chemical, quantity: BigInteger): Map<Chemical, BigInteger> {
        data class ChemicalRequirements(
            val requiredQuantity: BigInteger,
            val producedQuantity: BigInteger
        ) {
            fun addRequired(quantity: BigInteger): ChemicalRequirements {
                return copy(requiredQuantity = requiredQuantity + quantity)
            }

            fun addProduced(quantity: BigInteger): ChemicalRequirements {
                return copy(producedQuantity = producedQuantity + quantity)
            }
        }

        fun <K, V> Map<K, V>.put(key: K, value: V): Map<K, V> {
            return HashMap(this).apply {
                this[key] = value
            }
        }

        fun chemicalsToProduce(
            neededChemicals: Map<Chemical, ChemicalRequirements>,
            chemicalReactionItem: ChemicalReactionItem
        ): Map<Chemical, ChemicalRequirements> {
            val requirements = (neededChemicals[chemicalReactionItem.chemical] ?: ChemicalRequirements(BigInteger.ZERO, BigInteger.ZERO))
                .addRequired(chemicalReactionItem.quantity)
            return if (chemicalReactionItem.chemical !in RAW_MATERIALS && requirements.requiredQuantity > requirements.producedQuantity) {
                val neededQuantity = requirements.requiredQuantity - requirements.producedQuantity
                val reaction = reactions[chemicalReactionItem.chemical]
                    ?: throw error("Production reaction for $chemical is unknown")
                val reactionsNeeded = neededQuantity / reaction.outputChemical.quantity +
                    when (neededQuantity % reaction.outputChemical.quantity) {
                        BigInteger.ZERO -> BigInteger.ZERO
                        else -> BigInteger.ONE
                    }
                reaction.inputChemicals.chemicals()
                    .fold(neededChemicals) { neededCh, ch ->
                        chemicalsToProduce(neededCh, ch * reactionsNeeded)
                    }
                    .put(chemicalReactionItem.chemical, requirements.addProduced(reaction.outputChemical.quantity * reactionsNeeded))
            } else {
                neededChemicals.put(chemicalReactionItem.chemical, requirements)
            }
        }
        return chemicalsToProduce(mutableMapOf(), ChemicalReactionItem(chemical, quantity))
            .filterKeys { ch -> ch in RAW_MATERIALS }
            .mapValues { e -> e.value.requiredQuantity }
    }
}

inline class Chemical(val name: String)

val ORE = Chemical("ORE")

data class ChemicalReaction(
    val inputChemicals: InputChemicals,
    val outputChemical: ChemicalReactionItem
)

data class ChemicalReactionItem(
    val chemical: Chemical,
    val quantity: BigInteger
) {
    operator fun times(n: BigInteger): ChemicalReactionItem = ChemicalReactionItem(chemical, quantity * n)
}

class InputChemicals(private val chemicals: Set<ChemicalReactionItem>) {
    fun chemicals(): Set<ChemicalReactionItem> = chemicals
}
