package adventofcode2019.grid

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt

data class Angle(val x: Int, val y: Int) : Comparable<Angle> {
    private val radian: Double by lazy { normalised.radian }
    private val normalised: Vector by lazy { Vector(x.toDouble(), y.toDouble()).normalised }

    companion object {
        private data class Vector(val x: Double, val y: Double) {
            val radian: Double by lazy {
                val epsilon = 0.000001
                when {
                    x.absoluteValue < epsilon -> if (y > 0) 0.0 else -PI
                    x >= epsilon -> asin(y) - PI / 2
                    else -> PI / 2 - asin(y)
                }
            }
            val normalised: Vector by lazy {
                val length = sqrt(x.pow(2) + y.pow(2))
                Vector(x / length, y / length)
            }
        }
    }

    override fun compareTo(other: Angle): Int = radian.compareTo(other.radian)
}
