package api

import application.controller.GameController
import java.lang.IllegalStateException
import kotlin.random.Random

data class Point(var x: Int, var y: Int) {
    fun dirTo(other: Point): Direction {
        val result = Point(this.x - other.x, this.y - other.y)
        return when {
            result.x > 0 -> Direction.LEFT
            result.x < 0 -> Direction.RIGHT
            result.y > 0 -> Direction.UP
            result.y < 0 -> Direction.DOWN
            else -> throw IllegalStateException()
        }
    }
    
    fun move(direction: Direction): Point {
        when (direction) {
            Direction.UP -> this.y--
            Direction.RIGHT -> this.x++
            Direction.DOWN -> this.y++
            Direction.LEFT -> this.x--
        }
        return this
    }

    companion object {
        fun random() = Point(
            Random.nextInt(GameController.BOARD_WIDTH),
            Random.nextInt(GameController.BOARD_HEIGHT)
        )
    }
}