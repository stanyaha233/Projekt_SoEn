package uno

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class KartensetSpec extends AnyWordSpec with Matchers {

  "A Hand" should {
    
    "should start with zero cards" in {
      val hand = new Hand(List.empty)
      hand.anzahl shouldBe 0
    }

    "add a card correctly and update the count" in {
      val card = Karte(Farbe.Rot, Zahl.zero)
      val hand = new Hand(List.empty).add(card)

      hand.karten should contain (Karte)
      hand.anzahl shouldBe 1
    }

    "maintain all added cards in the order they were added" in {
      val card1 = Karte(Farbe.Rot, Zahl.zero)
      val card2 = Karte(Farbe.Schwarz, Zahl.plus4)

      val hand = new Hand(List.empty)
        .add(card1)
        .add(card2)

      hand.karten shouldBe List(card1, card2)
      hand.anzahl shouldBe 2
    }

    "be immutable (return a new Hand instance when adding cards)" in {
      val initialHand = new Hand(List.empty)
      val newHand = initialHand.add(Karte(Farbe.Blau, Zahl.fuenf))

      initialHand.anzahl shouldBe 0
      newHand.anzahl shouldBe 1
    }
  }
}