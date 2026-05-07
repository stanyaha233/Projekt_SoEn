package uno

import uno.model._
import uno.controller.UnoLogic
import uno.view.UnoPlay

@main def main(): Unit =
  val firstCard = Draw.draw()
  val initialState = GameState(
    playerHand = Draw.beginningHand(Hand(Nil)),
    cpuHand = Draw.beginningHand(Hand(Nil)),
    pile = firstCard,
    activeColour =
      if firstCard.colour == Colour.Black then Colour.Red
      else firstCard.colour,
    isPlayerTurn = true
  )

  val controller = new UnoLogic(initialState)
  val tui = new UnoPlay(controller)

  println("=== Willkommen zu UNO ===")
  controller.notifyObservers() // Initiale TUI Anzeige
  tui.readInput()              // Startet die Eingabe-Schleife