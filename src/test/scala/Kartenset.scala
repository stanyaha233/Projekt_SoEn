package uno

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class KartensetSpec extends AnyWordSpec with Matchers {

  "A Hand" should {
    
    "start with zero cards" in {
      val hand = new Hand(List.empty)
      hand.count shouldBe 0
    }

    "add a card correctly and update the count" in {
      val card = Card(Colour.Red, Number.zero)
      val hand = new Hand(List.empty).add(card)

      hand.cards should contain (card)
      hand.count shouldBe 1
    }

    "maintain all added cards in the order they were added" in {
      val card1 = Card(Colour.Red, Number.zero)
      val card2 = Card(Colour.Black, Number.plus4)

      val hand = new Hand(List.empty)
        .add(card1)
        .add(card2)

      hand.cards shouldBe List(card1, card2)
      hand.count shouldBe 2
    }

    "be immutable (return a new Hand instance when adding cards)" in {
      val initialHand = new Hand(List.empty)
      val newHand = initialHand.add(Card(Colour.Blue, Number.five))

      initialHand.count shouldBe 0
      newHand.count shouldBe 1
    }

    // --- Neue Tests für die moeglich-Methode ---

    "allow playing a card if the color matches" in {
      val hand = new Hand(List(Card(Colour.Red, Number.three)))
      val middle = Card(Colour.Red, Number.eight)
      
      hand.possible(middle) shouldBe true
    }

    "allow playing a card if the value (Zahl) matches" in {
      val hand = new Hand(List(Card(Colour.Blue, Number.eight)))
      val middle = Card(Colour.Red, Number.eight)
      
      hand.possible(middle) shouldBe true
    }

    "allow playing a card if it is a black (wild) card" in {
      val hand = new Hand(List(Card(Colour.Black, Number.plus4)))
      val middle = Card(Colour.Green, Number.five)
      
      hand.possible(middle) shouldBe true
    }

    "not allow playing a card if neither color nor value matches" in {
      val hand = new Hand(List(Card(Colour.Blue, Number.three)))
      val middle = Card(Colour.Red, Number.eight)
      
      hand.possible(middle) shouldBe false
    }

    "return false for moeglich if the hand is empty" in {
      val hand = new Hand(List.empty)
      val middle = Card(Colour.Red, Number.eight)
      
      hand.possible(middle) shouldBe false
    }
  }
}