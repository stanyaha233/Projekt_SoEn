package uno.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class StrategyServiceSpec extends AnyFlatSpec with Matchers {
  "DefaultStrategyService" should "sort cards by color and value, and delegate CPU decisions" in {
    val bot = new FirstPossibleStrategy
    val colorSorter = new SortByColorStrategy
    val valueSorter = new SortByValueStrategy
    val service = new DefaultStrategyService(bot, colorSorter, valueSorter)

    val hand = new Hand(List(Card(Colour.Red, Number.five), Card(Colour.Blue, Number.zero)))
    service.sortHandByColor(hand).cards should be(new Hand(colorSorter.sort(hand.cards)).cards)
    service.sortHandByValue(hand).cards should be(new Hand(valueSorter.sort(hand.cards)).cards)

    service.chooseCpuCard(hand, Colour.Red, Number.five) should be(Some(Card(Colour.Red, Number.five)))
  }
}
