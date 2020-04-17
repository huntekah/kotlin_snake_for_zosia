package implementation.fuzzy

import net.sourceforge.jFuzzyLogic.FIS

class LogicController {


    fun getStrategyType(len: Int, isVisible: Int): Double {
        val fis = FIS.load(javaClass.classLoader?.getResource("snake.fcl")?.path, true)
        if (fis != null) {
            fis.setVariable("dlugosc", len.toDouble())
            fis.setVariable("widocznosc_jablka", isVisible.toDouble())

            fis.evaluate()

            println("Value strategia  = ${fis.getVariable("strategia").value}")
            return fis.getVariable("strategia").value
        } else {
            println("NIE BANGLA WONSZ")
            return -1.0
        }
    }
}
