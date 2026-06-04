package uno

import uno.model._
import uno.controller.UnoLogic
import uno.aview.UnoPlay
import uno.util.GameFactory

object Main {
  def main(args: Array[String]): Unit = {
    val initialState = GameFactory.createInitialState()
    val controller = new UnoLogic(initialState)
    val tui = new UnoPlay(controller)

    println("=== Willkommen zu UNO ===")
    controller.notifyObservers() 
    tui.readInput()
  }
}