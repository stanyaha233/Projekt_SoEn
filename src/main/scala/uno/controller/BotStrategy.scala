package uno.controller

import uno.model._

trait BotStrategy {
  def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card]
}

class FirstPossibleStrategy extends BotStrategy {
  override def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card] =
    hand.cards.find(c => c.colour == activeColour || c.value == pileValue || c.colour == Colour.Black)
}