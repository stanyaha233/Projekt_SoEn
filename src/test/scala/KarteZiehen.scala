package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KarteZiehenSpec extends AnyFlatSpec with Matchers {

  "ziehen" should "generate a valid card with non-null properties" in {
    val card = Draw.draw()

    card.colour shouldNot be(null)
    card.value shouldNot be(null)
  }

  it should "only assign 'plus4' or 'wahl' to black cards" in {
    val draws = (1 to 100).map(_ => Draw.draw())
    val blackCards = draws.filter(_.colour == Colour.Black)

    blackCards.foreach { card =>
      card.value should (be(Number.plus4) or be(Number.choice))
    }
  }

  it should "never assign 'plus4' or 'wahl' to colored cards" in {
    val draws = (1 to 100).map(_ => Draw.draw())
    val coloredCards = draws.filter(_.colour != Colour.Black)

    coloredCards.foreach { card =>
      card.value shouldNot be(Number.plus4)
      card.value shouldNot be(Number.choice)
    }
  }

  "beginningHand" should "fill an empty hand to exactly 7 cards" in {
    val emptyHand = Hand(List.empty)
    val resultHand = Draw.beginningHand(emptyHand)

    resultHand.count should be(7)
  }

  it should "correctly supplement a partial hand to reach 7 cards" in {
    val initialHand = Hand(List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two)))
    initialHand.count should be(2)

    val resultHand = Draw.beginningHand(initialHand)
    resultHand.count should be(7)
  }

  it should "not add any cards if the hand already has 7 or more cards" in {
    val fullHand = Hand((1 to 7).map(_ => Draw.draw()).toList)
    val resultHand = Draw.beginningHand(fullHand)

    resultHand.count should be(7)
  }
}