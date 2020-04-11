package api

interface MovementDecider {
    fun decide(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction
}