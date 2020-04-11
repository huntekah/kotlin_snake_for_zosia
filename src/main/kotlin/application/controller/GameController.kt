package application.controller

import api.Direction
import api.MovementDecider
import api.Point
import api.Tile
import api.Tile.*
import implementation.RandomMovementDecider
import implementation.fuzzy.FuzzyLogicMovementDecider
import javafx.scene.input.KeyEvent
import tornadofx.Controller

class GameController : Controller() {

    private var isOn = true

    private val board = List(BOARD_WIDTH) {
        MutableList(BOARD_HEIGHT) {
            EMPTY
        }
    }

    private val snake = (0..4).map {
        Point(10 - it, 4)
    }.toMutableList()

    private var apple = Point(2, 3)

    val movementDeciders = mapOf(
        "Random" to RandomMovementDecider(),
        "Fuzzy" to FuzzyLogicMovementDecider(),
        "Manual" to object : MovementDecider {
            override fun decide(
                board: List<List<Tile>>,
                snake: List<Point>,
                apple: Point,
                forbiddenDirection: Direction
            ): Direction = lastDirection
        }
    )

    var lastDirection = Direction.RIGHT

    private var movementDecider = movementDeciders["Manual"] ?: error("No such mode! (Manual)")

    init {
        placeThings()
    }

    fun doMovement() {
        val direction = movementDecider.decide(
            board.map { it.toList() },
            snake.toList(),
            apple.copy(),
            getForbiddenDirection()
        )
        if (movementIsAllowed(direction)) {
            if (eatsApple(direction)) {
                snake.add(0, apple.copy())
                createNewApple()
            } else {
                snake.reversed().windowed(2).forEach {
                    it[0].x = it[1].x
                    it[0].y = it[1].y
                }
                snake.first().move(direction)
            }
            placeThings()
        } else {
            isOn = false
        }
    }

    private fun eatsApple(direction: Direction): Boolean =
        snake.first().copy().move(direction) == apple


    private fun createNewApple() {
        do {
            apple = Point.random()
        } while (snake.any { it == apple })
    }

    private fun movementIsAllowed(direction: Direction): Boolean {
        return snake.first().let { head ->
            when (direction) {
                Direction.UP -> head.y > 0
                Direction.RIGHT -> head.x < BOARD_WIDTH - 1
                Direction.DOWN -> head.y < BOARD_HEIGHT - 1
                Direction.LEFT -> head.x > 0
            }.and(
                snake.none {
                    it == head.copy().move(direction)
                }
            )
        }
    }

    fun getBoard() = board.toList().map { it.toList() }

    fun isOn() = isOn

    fun changeMode(newMode: String) {
        movementDecider = movementDeciders[newMode] ?: error("No such mode! ($newMode)")
    }

    fun keyPressed(key: KeyEvent) {
        val direction = when (key.text.toUpperCase()) {
            "W" -> Direction.UP
            "S" -> Direction.DOWN
            "A" -> Direction.LEFT
            "D" -> Direction.RIGHT
            else -> Direction.RIGHT
        }
        if (direction != getForbiddenDirection()) {
            lastDirection = direction
        }
    }

    private fun placeThings() {
        board.mapIndexed { rowNo, row ->
            row.mapIndexed { colNo, _ ->
                board[rowNo][colNo] = EMPTY
            }
        }
        snake.forEach { board[it.x][it.y] = SNAKE }
        board[apple.x][apple.y] = APPLE
    }

    private fun getForbiddenDirection(): Direction = snake[0].dirTo(snake[1])

    companion object {
        const val BOARD_WIDTH = 22
        const val BOARD_HEIGHT = 13
    }
}