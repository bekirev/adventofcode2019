package adventofcode2019.intcode

interface Memory {
    operator fun get(index: Int): Int
    operator fun set(index: Int, value: Int)
}

class ArrayMemory(private val array: IntArray) : Memory {
    companion object {
        fun ArrayMemory.copyOf(): ArrayMemory {
            return ArrayMemory(array.copyOf())
        }

        fun fromSequence(values: Sequence<Int>): ArrayMemory {
            return ArrayMemory(values.toList().toIntArray())
        }

        fun fromVararg(vararg values: Int): ArrayMemory {
            return ArrayMemory(values.copyOf())
        }
    }

    override fun get(index: Int): Int {
        return array[index]
    }

    override fun set(index: Int, value: Int) {
        array[index] = value
    }
}
