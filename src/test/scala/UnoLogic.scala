package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnoSpec extends AnyFlatSpec with Matchers {

  "A Hand" should "be initialized with the correct number of cards" in {
    val cards = List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two))
    val hand = new Hand(cards)
    hand.count should be (2)
  }

  it should "add a card correctly" in {
    val hand = new Hand(List(Card(Colour.Red, Number.one)))
    val newHand = hand.add(Card(Colour.Blue, Number.two))
    newHand.count should be (2)
    newHand.cards should contain (Card(Colour.Blue, Number.two))
  }

  it should "correctly report if a move is possible based on Colour" in {
    val topCard = Card(Colour.Red, Number.five)
    val hand = new Hand(List(Card(Colour.Red, Number.nine)))
    hand.possible(topCard) should be (true)
  }

  it should "correctly report if a move is possible based on Number" in {
    val topCard = Card(Colour.Blue, Number.five)
    val hand = new Hand(List(Card(Colour.Red, Number.five)))
    hand.possible(topCard) should be (true)
  }

  it should "correctly report if a move is possible based on Black Colour in hand" in {
    val topCard = Card(Colour.Red, Number.five)
    val hand = new Hand(List(Card(Colour.Black, Number.choice)))
    hand.possible(topCard) should be (true)
  }

  it should "report move not possible if no matching card" in {
    val topCard = Card(Colour.Red, Number.five)
    val hand = new Hand(List(Card(Colour.Blue, Number.one), Card(Colour.Green, Number.Skip)))
    hand.possible(topCard) should be (false)
  }

  "The Card Drawing Logic" should "provide exactly 7 cards for a beginning hand" in {
    val emptyHand = new Hand(List.empty[Card])
    val fullHand = Draw.beginningHand(emptyHand)
    fullHand.count should be (7)
  }

  it should "add cards to an existing hand until it has 7 cards" in {
    val initialHand = new Hand(List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two)))
    val fullHand = Draw.beginningHand(initialHand)
    fullHand.count should be (7)
  }

  it should "return a valid Card when draw() is called" in {
    val card = Draw.draw()
    card should not be null
  }

  it should "never draw a +4 or choice card with non-black colour" in {
    for (_ <- 1 to 100) {
      val card = Draw.draw()
      if (card.colour != Colour.Black) {
        card.value should not be (Number.plus4)
        card.value should not be (Number.choice)
      } else {
        Seq(Number.choice, Number.plus4) should contain (card.value)
      }
    }
  }

  "A Card" should "be correctly instantiated" in {
    val card = Card(Colour.Yellow, Number.Skip)
    card.colour should be (Colour.Yellow)
    card.value should be (Number.Skip)
  }
}