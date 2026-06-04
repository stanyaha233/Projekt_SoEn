package uno.controller

import uno.model._

trait CpuStrategy {
  def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card]
}

class FirstPossibleStrategy extends CpuStrategy {
  override def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card] =
    hand.cards.find(c => c.colour == activeColour || c.value == pileValue || c.colour == Colour.Black)
}