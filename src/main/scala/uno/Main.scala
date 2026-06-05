package uno

import uno.model._
import uno.controller.UnoLogic
import uno.aview.UnoPlay
import uno.gui.SwingGui // Importiere deine neue GUI
import uno.util.GameFactory
import javax.swing.UIManager

object Main {
  def main(args: Array[String]): Unit = {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName)
    val initialState = GameFactory.createInitialState()
    val controller = new UnoLogic(initialState)

    // 1. TUI erstellen
    val tui = new UnoPlay(controller)

    // 2. GUI erstellen (registriert sich selbst beim Controller)
    val gui = new SwingGui(controller)

    println("=== Willkommen zu UNO ===")

    // Initialer Aufruf, um TUI und GUI einmalig zu zeichnen
    controller.notifyObservers()

    // 3. TUI-Input-Loop starten
    tui.readInput()
  }
}