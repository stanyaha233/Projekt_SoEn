package uno.util

import uno.model.*
import uno.model.components.GameState

object GameFactory {
  def createInitialState(): GameState = {
    val firstCard = Draw.draw()
    GameState(
      playerHand = Draw.beginningHand(new Hand(Nil)),
      cpuHand = Draw.beginningHand(new Hand(Nil)),
      pile = firstCard,
      activeColour = if (firstCard.colour == Colour.Black) Colour.Red else firstCard.colour,
      isPlayerTurn = true
    )
  }
}

