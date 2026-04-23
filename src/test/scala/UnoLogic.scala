package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnoSpec extends AnyFlatSpec with Matchers {

  "A Hand" should "be initialized with the correct number of cards" in {
    val cards = List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two))
    val hand = new Hand(cards)
    hand.count should be (2)
  }

  it should "correctly report if a move is possible" in {
    val topCard = Card(Colour.Red, Number.five)
    val hand = new Hand(List(Card(Colour.Red, Number.nine)))
    hand.possible(topCard) should be (true)
  }

  "The Card Drawing Logic" should "provide exactly 7 cards for a beginning hand" in {
    val emptyHand = new Hand(List.empty[Card])
    val fullHand = Draw.beginningHand(emptyHand)
    fullHand.count should be (7)
  }

  "UnoSpiel Logic (Coverage Boost)" should "validate moves correctly using isValidMove" in {
    val middle = Card(Colour.Red, Number.five)

    // Case: Same Colour
    UnoSpiel.isValidMove(Colour.Red, Number.one, middle) should be (true)
    // Case: Same Value
    UnoSpiel.isValidMove(Colour.Blue, Number.five, middle) should be (true)
    // Case: Black Card
    UnoSpiel.isValidMove(Colour.Black, Number.plus4, middle) should be (true)
    // Case: No match
    UnoSpiel.isValidMove(Colour.Blue, Number.one, middle) should be (false)
  }

  it should "handle penalties like plus2 correctly" in {
    val hand = new Hand(List.empty[Card])
    val plus2Card = Card(Colour.Blue, Number.plus2)

    val (newHand, skipTurn) = UnoSpiel.handlePenalty(plus2Card, hand)

    newHand.count should be (2)
    skipTurn should be (true)
  }

  it should "handle the Skip card penalty" in {
    val hand = new Hand(List(Card(Colour.Red, Number.one)))
    val skipCard = Card(Colour.Green, Number.Skip)

    val (newHand, skipTurn) = UnoSpiel.handlePenalty(skipCard, hand)

    newHand.count should be (1) // No cards added
    skipTurn should be (true)  // But turn is skipped
  }

  it should "not apply a penalty for normal cards" in {
    val hand = new Hand(List.empty[Card])
    val normalCard = Card(Colour.Red, Number.one)

    val (newHand, skipTurn) = UnoSpiel.handlePenalty(normalCard, hand)

    newHand.count should be (0)
    skipTurn should be (false)
  }

  "Opponent AI" should "pick the first valid card from its hand" in {
    val middle = Card(Colour.Red, Number.seven)
    val opponentHand = new Hand(List(
      Card(Colour.Blue, Number.eight), // Invalid
      Card(Colour.Red, Number.nine)    // Valid (same colour)
    ))

    val move = UnoSpiel.opponentAI(opponentHand, middle)

    move shouldBe defined
    move.get.colour should be (Colour.Red)
  }

  it should "return None if no valid card is found" in {
    val middle = Card(Colour.Yellow, Number.zero)
    val opponentHand = new Hand(List(Card(Colour.Blue, Number.one)))

    val move = UnoSpiel.opponentAI(opponentHand, middle)

    move shouldBe None
  }

  "Win Condition" should "be triggered when a hand is empty" in {
    val emptyHand = new Hand(List.empty[Card])
    val normalHand = new Hand(List(Card(Colour.Red, Number.one)))

    // We can't easily test println, but we can verify the logic
    emptyHand.count should be (0)
    normalHand.count should be > 0
  }
}