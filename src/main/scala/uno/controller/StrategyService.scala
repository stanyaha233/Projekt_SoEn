package uno.controller

import com.google.inject.Inject
import com.google.inject.name.Named
import uno.model._

trait StrategyService {
  def sortHandByColor(hand: Hand): Hand
  def sortHandByValue(hand: Hand): Hand
  def chooseCpuCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card]
}

class DefaultStrategyService @Inject() (
    private val botStrategy: BotStrategy,
    @Named("colorSorter") private val colorSorter: SortingStrategy,
    @Named("valueSorter") private val valueSorter: SortingStrategy
) extends StrategyService {
  override def sortHandByColor(hand: Hand): Hand = new Hand(colorSorter.sort(hand.cards))

  override def sortHandByValue(hand: Hand): Hand = new Hand(valueSorter.sort(hand.cards))

  override def chooseCpuCard(hand: Hand, activeColour: Colour.Value, pileValue: Number.Value): Option[Card] =
    botStrategy.chooseCard(hand, activeColour, pileValue)
}