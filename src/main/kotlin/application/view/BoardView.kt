package application.view

import api.Tile
import api.Tile.*
import application.controller.GameController
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import tornadofx.*

class BoardView : View() {

    private val controller: GameController by inject()

    lateinit var tiles: List<List<Rectangle>>

    private val mode = SimpleStringProperty()

    init {
        mode.onChange { newMode ->
            newMode?.let { controller.changeMode(it) }
        }
    }

    override val root = borderpane {
        addEventFilter(KeyEvent.KEY_PRESSED) {
            controller.keyPressed(it)
        }
        style {
            backgroundColor += COLOR_BG
        }
        top = hbox {
            text {
                text = "Mode: "
                alignment = Pos.CENTER_LEFT
            }
            combobox(
                values = controller.movementDeciders.keys.toList(),
                property = mode
            ) {
                value = "Zosia"
            }
        }
        center = pane {
            requestFocus()
            style {
                borderWidth += box(6.px)
                borderColor += box(COLOR_FG)
            }
            tiles = controller.getBoard()
                .mapIndexed { rowNo, row ->
                    row.mapIndexed { colNo, tile ->
                        rectangle {
                            width = TILE_WIDTH.minus(2)
                            height = TILE_HEIGHT.minus(2)
                            x = 7 + rowNo * TILE_WIDTH.plus(1)
                            y = 7 + colNo * TILE_HEIGHT.plus(1)
                            fill = getColor(tile)
                        }
                    }
                }
            timeline {
                keyframe(Duration.seconds(0.02)) {
                    setOnFinished {
                        if (controller.isOn()) {
                            controller.getBoard()
                                .mapIndexed { rowNo, row ->
                                    row.mapIndexed { colNo, tile ->
                                        tiles[rowNo][colNo].fill = getColor(tile)
                                    }
                                }
                            controller.doMovement()
                        } else {
                            this@pane.text {
                                textAlignment = TextAlignment.CENTER
                                text = "GAME OVER"
                            }
                        }
                    }
                }
                isAutoReverse = true
                cycleCount = Timeline.INDEFINITE
            }

        }
    }

    private fun getColor(tile: Tile): Color {
        return when (tile) {
            SNAKE -> COLOR_FG
            APPLE -> COLOR_APPLE
            EMPTY -> COLOR_BG
        }
    }

    companion object {
        var TILE_WIDTH = 40.0
        var TILE_HEIGHT = 40.0

        val COLOR_APPLE = c("#FF0A0A")
        val COLOR_BG = c("#95C50C")
        val COLOR_FG = c("#435A13")
    }
}
