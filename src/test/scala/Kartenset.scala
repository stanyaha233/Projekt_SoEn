package uno

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class KartensetSpec extends AnyWordSpec with Matchers {

  "A Hand" should {
    
    "start with zero cards" in {
      val hand = new Hand(List.empty)
      hand.anzahl shouldBe 0
    }

    "add a card correctly and update the count" in {
      val card = Karte(Farbe.Rot, Zahl.zero)
      val hand = new Hand(List.empty).add(card)

      hand.karten should contain (card)
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

    // --- Neue Tests für die moeglich-Methode ---

    "allow playing a card if the color matches" in {
      val hand = new Hand(List(Karte(Farbe.Rot, Zahl.drei)))
      val mitte = Karte(Farbe.Rot, Zahl.acht)
      
      hand.moeglich(mitte) shouldBe true
    }

    "allow playing a card if the value (Zahl) matches" in {
      val hand = new Hand(List(Karte(Farbe.Blau, Zahl.acht)))
      val mitte = Karte(Farbe.Rot, Zahl.acht)
      
      hand.moeglich(mitte) shouldBe true
    }

    "allow playing a card if it is a black (wild) card" in {
      val hand = new Hand(List(Karte(Farbe.Schwarz, Zahl.plus4)))
      val mitte = Karte(Farbe.Gruen, Zahl.fuenf)
      
      hand.moeglich(mitte) shouldBe true
    }

    "not allow playing a card if neither color nor value matches" in {
      val hand = new Hand(List(Karte(Farbe.Blau, Zahl.drei)))
      val mitte = Karte(Farbe.Rot, Zahl.acht)
      
      hand.moeglich(mitte) shouldBe false
    }

    "return false for moeglich if the hand is empty" in {
      val hand = new Hand(List.empty)
      val mitte = Karte(Farbe.Rot, Zahl.acht)
      
      hand.moeglich(mitte) shouldBe false
    }
  }
}