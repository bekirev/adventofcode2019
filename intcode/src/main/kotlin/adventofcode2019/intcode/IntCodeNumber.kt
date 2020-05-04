package adventofcode2019.intcode

import java.math.BigInteger

class IntCodeNumber private constructor(private val value: BigInteger): Comparable<IntCodeNumber> {
    companion object {
        val ZERO = IntCodeNumber(BigInteger.ZERO)
        val ONE = IntCodeNumber(BigInteger.ONE)
        val TWO = IntCodeNumber(BigInteger.TWO)

        fun of(value: Int): IntCodeNumber {
            return IntCodeNumber(BigInteger.valueOf(value.toLong()))
        }

        fun of(value: Long): IntCodeNumber {
            return IntCodeNumber(BigInteger.valueOf(value))
        }

        fun of(str: String): IntCodeNumber = IntCodeNumber(BigInteger(str))
    }

    operator fun plus(other: IntCodeNumber): IntCodeNumber {
        return IntCodeNumber(value.add(other.value))
    }

    operator fun times(other: IntCodeNumber): IntCodeNumber {
        return IntCodeNumber(value.multiply(other.value))
    }

    fun toInt(): Int = value.toInt()

    fun toLong(): Long = value.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntCodeNumber

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun compareTo(other: IntCodeNumber): Int {
        return value.compareTo(other.value)
    }

    override fun toString(): String {
        return "IntCodeNumber(value=$value)"
    }
}