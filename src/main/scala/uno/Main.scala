package uno

import uno.model._
import uno.controller.{ControllerInterface, UnoLogic}
import uno.aview.{UnoPlay, TuiInterface}
import uno.gui.SwingGui
import uno.util.GameFactory
import javax.swing.UIManager
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName)
    val initialState = GameFactory.createInitialState()
    
    // 1. Controller über Interface kapseln
    val controller: ControllerInterface = new UnoLogic(initialState)

    // 2. TUI erstellen und nur das Interface zugänglich machen
    val tui: TuiInterface = new UnoPlay(controller)

    // 3. GUI erstellen
    val gui = new SwingGui(controller)

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