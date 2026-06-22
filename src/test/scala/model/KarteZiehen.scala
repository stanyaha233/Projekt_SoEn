package uno.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class KarteZiehenSpec extends AnyWordSpec with Matchers {

  "DeckFactory" should {
    "create a standard deck of 108 cards" in {
      val deck = DeckFactory.createStandardDeck()
      deck.length shouldBe 108
      deck.count(_.colour == Colour.Black) shouldBe 8 // 4x choice, 4x plus4
    }
  }

  "Draw" should {
    "replenish pile if empty" in {
      Draw.setDeck(List.empty)
      val card = Draw.draw()
      Draw.pileSize shouldBe 107 // 108 neu generiert - 1 gezogene Karte
      Draw.getDeck.length shouldBe 107
    }

    "draw from a non-empty pile without replenishing it" in {
      val topCard = Card(Colour.Green, Number.seven)
      Draw.setDeck(List(topCard))

      val drawnCard = Draw.draw()

      drawnCard shouldBe topCard
      Draw.pileSize shouldBe 0
    }

    "create a beginning hand of 7 cards" in {
      Draw.setDeck(DeckFactory.createStandardDeck())
      val hand = Draw.beginningHand(new Hand(List.empty))
      hand.count shouldBe 7
    }

    "supplement a partial hand to reach 7 cards" in {
      Draw.setDeck(DeckFactory.createStandardDeck())
      val initialHand = Hand(List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two)))
      val resultHand = Draw.beginningHand(initialHand)
      resultHand.count shouldBe 7
    }

    "not add any cards if the hand already has 7 or more cards" in {
      Draw.setDeck(DeckFactory.createStandardDeck())
      val fullHand = Hand((1 to 8).map(_ => Card(Colour.Red, Number.one)).toList)
      val resultHand = Draw.beginningHand(fullHand)
      resultHand.count shouldBe 8
    }
  }
}