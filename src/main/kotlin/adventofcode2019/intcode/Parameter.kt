package adventofcode2019.intcode

sealed class Parameter(open val value: Int) {
    data class BasicParameter(override val value: Int) : Parameter(value)
    data class ComplexParameter(override val value: Int, val parameterMode: ParameterMode) : Parameter(value)
}

enum class ParameterMode {
    POSITION_MODE, IMMEDIATE_MODE
}

interface InstructionParameters {
    fun size(): Int
    operator fun get(index: Int): Parameter
}

object EmptyInstructionParameters : InstructionParameters {
    override fun size(): Int = 0
    override fun get(index: Int): Parameter = throw IllegalArgumentException("No argument at index: $index")
}

class ArrayInstructionParameters private constructor(private val args: Array<Parameter>) : InstructionParameters {
    companion object {
        fun fromArray(args: Array<Parameter>): ArrayInstructionParameters {
            return ArrayInstructionParameters(args.copyOf())
        }

        fun fromList(args: List<Parameter>): ArrayInstructionParameters {
            return ArrayInstructionParameters(args.toTypedArray())
        }
    }
    override fun size(): Int = args.size
    override fun get(index: Int): Parameter = args[index]
}
