package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KarteSpec extends AnyFlatSpec with Matchers {

  "A Karte" should "properly store its color and value" in {
    val card = Card(Colour.Red, Number.seven)
    card.colour should be(Colour.Red)
    card.value should be(Number.seven)
  }

  "Move logic" should "allow a move if the color matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Red, Number.three)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(true)
  }

  it should "allow a move if the value matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Blue, Number.seven)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(true)
  }

  it should "deny a move if neither color nor value matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Blue, Number.three)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(false)
  }

  it should "detect at least one playable card in a hand" in {
    val topCard = Card(Colour.Red, Number.seven)
    val hand = List(
      Card(Colour.Blue, Number.three),
      Card(Colour.Green, Number.Skip),
      Card(Colour.Red, Number.two)
    )

    val possible = hand.exists(k => k.colour == topCard.colour || k.value == topCard.value)
    possible should be(true)
  }

  it should "handle special black cards correctly (Wild cards)" in {
    val topCard = Card(Colour.Red, Number.seven)
    val wildCard = Card(Colour.Black, Number.choice)
    val possible = wildCard.colour == topCard.colour || wildCard.value == topCard.value

    possible should be(false)
  }
}