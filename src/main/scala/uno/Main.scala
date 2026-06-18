package uno

import uno.model._
import uno.controller.ControllerInterface
import uno.aview.{UnoPlay, TuiInterface}
import uno.gui.SwingGui
import javax.swing.UIManager
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName)
    
    val injector = com.google.inject.Guice.createInjector(new UnoModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = injector.getInstance(classOf[TuiInterface])
    val gui = injector.getInstance(classOf[SwingGui])

    println("=== Willkommen zu UNO ===")

    // Initialer Aufruf, um TUI und GUI einmalig zu zeichnen
    controller.notifyObservers()

    // 4. Input-Loop läuft separiert vom UI, ruft lediglich die Interface-Methode auf
    while (controller.state.isGameActive) {
      if (controller.state.isPlayerTurn) {
        val input = StdIn.readLine("Zug (Farbe Wert) oder 'draw': ")
        if (input != null) {
          tui.processInputLine(input.trim.toLowerCase)
        }
      } else {
        Thread.sleep(1000)
        controller.cpuTurn()
      }
    }
  }
}