package adventofcode2019.day11

import adventofcode2019.day11.Color.BLACK
import adventofcode2019.day11.Color.WHITE
import adventofcode2019.day11.RobotDirection.DOWN
import adventofcode2019.day11.RobotDirection.LEFT
import adventofcode2019.day11.RobotDirection.RIGHT
import adventofcode2019.day11.RobotDirection.UP
import adventofcode2019.grid.Angle
import adventofcode2019.grid.ArrayGrid
import adventofcode2019.grid.Bounds
import adventofcode2019.grid.Position
import adventofcode2019.intcode.getIntCodeInput
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths

fun main() {
    val paintedPositionsStartingOnBlack = createAndRunRobotOnColor(BLACK)
    println(paintedPositionsStartingOnBlack.paintedPlaces.size)
    val paintedPositionsStartingOnWhite = createAndRunRobotOnColor(WHITE)
    println(paintedPositionsStartingOnWhite.grid.toString { color -> when (color) {
        BLACK -> "."
        WHITE -> "#"
    } })
}

private fun createAndRunRobotOnColor(firstPlateColor: Color): RobotWorkResult {
    return runBlocking {
        val robot = Robot.fromMemory(getIntCodeInput(Paths.get("adventofcode2019", "day11", "input.txt")))
        val robotJob = launch { robot.run() }
        var robotPosition = Position(0, 0)
        var robotDirection = UP
        val grid: ArrayGrid<Color> = ArrayGrid.withBounds(Bounds(-65, 45, -20, 55)) { BLACK }.apply {
            changeElements(sequenceOf(robotPosition to firstPlateColor))
        }
        val paintedPlatesPositions: MutableSet<Position> = HashSet()
        val moveRobotJob = launch {
            while (isActive) {
                val originalPlateColor = grid[robotPosition]
                robot.panelColor(originalPlateColor)
                val (paintColor, turnDirection) = robot.output()
                paintedPlatesPositions.add(robotPosition)
                grid.changeElements(sequenceOf(robotPosition to paintColor))
                robotDirection = robotDirection.turn(turnDirection)
                robotPosition += robotDirection.angle
            }
        }
        robotJob.join()
        moveRobotJob.cancelAndJoin()
        RobotWorkResult(grid, paintedPlatesPositions)
    }
}

data class RobotWorkResult(
    val grid: ArrayGrid<Color>,
    val paintedPlaces: Set<Position>
)

private enum class RobotDirection(val angle: Angle) {
    UP(Angle(0, 1)),
    RIGHT(Angle(1, 0)),
    DOWN(Angle(0, -1)),
    LEFT(Angle(-1, 0))
}

private fun RobotDirection.turn(turnDirection: TurnDirection): RobotDirection = when (this) {
    UP -> when (turnDirection) {
        TurnDirection.LEFT -> LEFT
        TurnDirection.RIGHT -> RIGHT
    }
    RIGHT -> when (turnDirection) {
        TurnDirection.LEFT -> UP
        TurnDirection.RIGHT -> DOWN
    }
    DOWN -> when (turnDirection) {
        TurnDirection.LEFT -> RIGHT
        TurnDirection.RIGHT -> LEFT
    }
    LEFT -> when (turnDirection) {
        TurnDirection.LEFT -> DOWN
        TurnDirection.RIGHT -> UP
    }
}
