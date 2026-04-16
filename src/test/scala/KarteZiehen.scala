package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KarteZiehenSpec extends AnyFlatSpec with Matchers {

  "ziehen" should "generate a valid card with non-null properties" in {
    val card = KarteZiehen.ziehen()

    card.farbe shouldNot be(null)
    card.wert shouldNot be(null)
  }

  it should "only assign 'plus4' or 'wahl' to black cards" in {
    val draws = (1 to 100).map(_ => KarteZiehen.ziehen())
    val blackCards = draws.filter(_.farbe == Farbe.Schwarz)

    blackCards.foreach { card =>
      card.wert should (be(Zahl.plus4) or be(Zahl.wahl))
    }
  }

  it should "never assign 'plus4' or 'wahl' to colored cards" in {
    val draws = (1 to 100).map(_ => KarteZiehen.ziehen())
    val coloredCards = draws.filter(_.farbe != Farbe.Schwarz)

    coloredCards.foreach { card =>
      card.wert shouldNot be(Zahl.plus4)
      card.wert shouldNot be(Zahl.wahl)
    }
  }

  "anfangskarten" should "fill an empty hand to exactly 7 cards" in {
    val emptyHand = Hand(List.empty)
    val resultHand = KarteZiehen.anfangskarten(emptyHand)

    resultHand.anzahl should be(7)
  }

  it should "correctly supplement a partial hand to reach 7 cards" in {
    val initialHand = Hand(List(Karte(Farbe.Rot, Zahl.eins), Karte(Farbe.Blau, Zahl.zwei)))
    initialHand.anzahl should be(2)

    val resultHand = KarteZiehen.anfangskarten(initialHand)
    resultHand.anzahl should be(7)
  }

  it should "not add any cards if the hand already has 7 or more cards" in {
    val fullHand = Hand((1 to 7).map(_ => KarteZiehen.ziehen()).toList)
    val resultHand = KarteZiehen.anfangskarten(fullHand)

    resultHand.anzahl should be(7)
  }
}