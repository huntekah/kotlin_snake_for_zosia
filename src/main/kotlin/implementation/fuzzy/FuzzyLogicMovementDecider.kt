package implementation.fuzzy

import api.Direction
import api.MovementDecider
import api.Point
import api.Tile

class FuzzyLogicMovementDecider: MovementDecider {

    val logicController = LogicController()

    override fun decide(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction {
        // TODO: implement this
        return Direction.RIGHT
    }
}