package implementation.fuzzy

import net.sourceforge.jFuzzyLogic.FIS

class LogicController {

    fun test() {
        val fis = FIS.load(javaClass.classLoader?.getResource("napiwek.fcl")?.path, true)
        if (fis != null) {
            fis.setVariable("obsluga", 3.0)
            fis.setVariable("jedzenie", 8.0)

            fis.evaluate()

            println("Value = ${fis.getVariable("napiwek").value}")
        } else {
            println("NIE BANGLA")
        }
    }
}