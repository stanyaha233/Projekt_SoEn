package uno

import uno.model._
import uno.controller.UnoLogic
import uno.view.gameLoop

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

  println("=== Willkommen zu UNO ===")
  gameLoop(initialState)