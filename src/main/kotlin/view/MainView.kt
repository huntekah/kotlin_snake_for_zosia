package view

import tornadofx.*
import tornadofx.Stylesheet.Companion.button

class MainView: View() {

    override val root = vbox {
        button("Press me")
    }
}
