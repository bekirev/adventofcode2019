package adventofcode2019.day12

import java.lang.System.lineSeparator
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val io = Body(
        Position(1, 4, 4),
        Velocity.ZERO
    )
    val europa = Body(
        Position(-4, -1, 19),
        Velocity.ZERO
    )
    val ganymede = Body(
        Position(-15, -14, 12),
        Velocity.ZERO
    )
    val callisto = Body(
        Position(-17, 1, 10),
        Velocity.ZERO
    )
    var system = System(setOf(io, europa, ganymede, callisto))
    var i = 0
    val states: MutableMap<System, Int> = HashMap(Int.MAX_VALUE)
    states[system] = i
    while (i <= 1000) {
        ++i
        system = system.step()
        states[system] = i
    }
    println(system.totalEnergy)
    while (true) {
        ++i
        system = system.step()
        if (states[system] != null) {
            break
        }
        if (i % 10000 == 0)
            println(i)
    }
    println(states[system]!! - i)
}

private class System(private val bodies: Set<Body>) {
    val totalEnergy: Int by lazy {
        bodies
            .asSequence()
            .map(Body::totalEnergy)
            .sum()
    }

    fun step(): System {
        return System(
            bodies
                .map { body ->
                    val acceleration = bodies
                        .asSequence()
                        .filter { otherBody -> otherBody != body }
                        .map { otherBody -> gravity(body, otherBody) }
                        .fold(Acceleration.ZERO) { resultingAcceleration: Acceleration, acceleration: Acceleration ->
                            resultingAcceleration + acceleration
                        }
                    val velocity = body.velocity + acceleration
                    val position = body.position + velocity
                    body.copy(position = position, velocity = velocity)
                }
                .toSet()
        )
    }

    override fun toString(): String {
        return "System(bodies=[${lineSeparator()}${bodies.joinToString(lineSeparator())}${lineSeparator()}])"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as System

        if (bodies != other.bodies) return false

        return true
    }

    override fun hashCode(): Int {
        return bodies.hashCode()
    }
}

private data class Body(
    val position: Position,
    val velocity: Velocity
) {
    private val potentialEnergy: Int by lazy {
        position.x.absoluteValue + position.y.absoluteValue + position.z.absoluteValue
    }
    private val kineticEnergy: Int by lazy {
        velocity.x.absoluteValue + velocity.y.absoluteValue + velocity.z.absoluteValue
    }
    val totalEnergy: Int by lazy {
        potentialEnergy * kineticEnergy
    }
}

private data class Position(
    val x: Int,
    val y: Int,
    val z: Int
) {
    operator fun plus(vel: Velocity): Position = Position(x + vel.x, y + vel.y, z + vel.z)
}

private data class Velocity(
    val x: Int,
    val y: Int,
    val z: Int
) {
    companion object {
        val ZERO = Velocity(0, 0, 0)
    }

    operator fun plus(acc: Acceleration): Velocity = Velocity(x + acc.x, y + acc.y, z + acc.z)
}

private data class Acceleration(
    val x: Int,
    val y: Int,
    val z: Int
) {
    companion object {
        val ZERO = Acceleration(0, 0, 0)
    }
    operator fun unaryMinus(): Acceleration = Acceleration(-x, -y, -z)
    operator fun plus(other: Acceleration): Acceleration = Acceleration(x + other.x, y + other.y, z + other.z)
}

private fun gravity(bodyToPull: Body, otherBody: Body): Acceleration {
    fun gravityForAxis(pos1: Int, pos2: Int): Int {
        return (pos2 - pos1).sign
    }

    fun gravity(pos1: Position, pos2: Position): Acceleration {
        return Acceleration(
            gravityForAxis(pos1.x, pos2.x),
            gravityForAxis(pos1.y, pos2.y),
            gravityForAxis(pos1.z, pos2.z)
        )
    }
    return gravity(bodyToPull.position, otherBody.position)
}
