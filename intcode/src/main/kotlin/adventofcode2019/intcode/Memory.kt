package adventofcode2019.intcode

interface Memory {
    operator fun get(index: Int): IntCodeNumber
    operator fun set(index: Int, value: IntCodeNumber)
    fun size(): Int
}

class ArrayMemory constructor(private val array: Array<IntCodeNumber>) : Memory {
    companion object {
        fun ArrayMemory.copyOf(): ArrayMemory {
            return ArrayMemory(array.copyOf())
        }

        fun of(values: Sequence<IntCodeNumber>): ArrayMemory {
            return ArrayMemory(values.toList().toTypedArray())
        }

        fun of(vararg values: Int): ArrayMemory {
            return ArrayMemory(Array(values.size) { index -> IntCodeNumber.of(values[index]) })
        }

        fun of(vararg values: Long): ArrayMemory {
            return ArrayMemory(Array(values.size) { index -> IntCodeNumber.of(values[index]) })
        }

        fun of(list: List<IntCodeNumber>): ArrayMemory {
            return ArrayMemory(list.toTypedArray())
        }
    }

    override fun get(index: Int): IntCodeNumber {
        return array[index]
    }

    override fun set(index: Int, value: IntCodeNumber) {
        array[index] = value
    }

    override fun size(): Int {
        return array.size
    }
}

class AdditionalMapMemory private constructor(private val baseMemory: Memory, private val map: MutableMap<Int, IntCodeNumber>) : Memory {
    companion object {
        fun fromMemory(baseMemory: Memory): AdditionalMapMemory = AdditionalMapMemory(baseMemory, HashMap())
    }

    override fun get(index: Int): IntCodeNumber = when {
        index < baseMemory.size() -> baseMemory[index]
        else -> map[index] ?: IntCodeNumber.ZERO
    }

    override fun set(index: Int, value: IntCodeNumber) = when {
        index < baseMemory.size() -> baseMemory[index] = value
        else -> map[index] = value
    }

    override fun size(): Int = Int.MAX_VALUE
}
