package uno.controller

import uno.model._

trait BotStrategy {
  def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card]
}

class FirstPossibleStrategy extends BotStrategy {
  override def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card] =
    hand.cards.find(c => c.colour == activeColour || c.value == pileValue || c.colour == Colour.Black)
}

class BlackFirstStrategy extends BotStrategy {
  override def chooseCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card] =
    hand.cards.find(c => c.colour == Colour.Black) match {
      case Some(blackCard) => Some(blackCard)
      case None => hand.cards.find(c => c.colour == activeColour || c.value == pileValue)
    }
}