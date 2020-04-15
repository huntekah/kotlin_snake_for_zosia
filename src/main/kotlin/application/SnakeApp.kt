package application

import application.controller.GameController
import javafx.stage.Stage
import tornadofx.*
import application.view.BoardView

class SnakeApp: App(BoardView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        //stage.isResizable = false // Make workable on Arch linux
    }
}