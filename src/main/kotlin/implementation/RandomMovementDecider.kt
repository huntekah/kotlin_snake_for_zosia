package implementation

import api.Direction
import api.MovementDecider
import api.Point
import api.Tile

class RandomMovementDecider: MovementDecider {
    override fun decide(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction {
        return Direction.random(forbiddenDirection)
    }
}