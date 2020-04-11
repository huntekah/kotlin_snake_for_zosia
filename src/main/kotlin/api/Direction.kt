package api

import java.util.*

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        fun random() = values().toMutableList().run {
            get(Random().nextInt(size))
        }

        fun random(forbiddenDirection: Direction) = values().toMutableList().run {
            remove(forbiddenDirection)
            get(Random().nextInt(size))
        }
    }
}

