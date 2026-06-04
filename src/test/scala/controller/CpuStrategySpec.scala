package uno.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class CpuStrategySpec extends AnyFlatSpec with Matchers {
  "FirstPossibleStrategy" should "return the first valid card matching colour" in {
    val strategy = new FirstPossibleStrategy()
    val hand = new Hand(List(Card(Colour.Blue, Number.nine), Card(Colour.Red, Number.five)))
    
    val result = strategy.chooseCard(hand, Colour.Red, Number.zero)
    result should be(Some(Card(Colour.Red, Number.five)))
  }

  it should "return the first valid card matching value" in {
    val strategy = new FirstPossibleStrategy()
    val hand = new Hand(List(Card(Colour.Blue, Number.nine), Card(Colour.Red, Number.five)))
    
    val result = strategy.chooseCard(hand, Colour.Yellow, Number.five)
    result should be(Some(Card(Colour.Red, Number.five)))
  }

  it should "return a black card if no other colour or value matches" in {
    val strategy = new FirstPossibleStrategy()
    val hand = new Hand(List(Card(Colour.Black, Number.choice), Card(Colour.Red, Number.five)))
    
    val result = strategy.chooseCard(hand, Colour.Yellow, Number.zero)
    result should be(Some(Card(Colour.Black, Number.choice)))
  }

  it should "return None if absolutely no cards match" in {
    val strategy = new FirstPossibleStrategy()
    val hand = new Hand(List(Card(Colour.Blue, Number.nine), Card(Colour.Red, Number.five)))
    
    val result = strategy.chooseCard(hand, Colour.Yellow, Number.zero)
    result should be(None)
  }
}