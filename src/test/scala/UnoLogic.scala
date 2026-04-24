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
    newHand.cards.exists(c => c.colour == Colour.Blue && c.value == Number.two) should be (true)
  }

  it should "correctly report if a move is possible" in {
    val hand = new Hand(List(Card(Colour.Red, Number.nine)))
    hand.possible(Card(Colour.Red, Number.five)) should be (true)
    hand.possible(Card(Colour.Blue, Number.nine)) should be (true)
    hand.possible(Card(Colour.Blue, Number.five)) should be (false)
    val blackHand = new Hand(List(Card(Colour.Black, Number.choice)))
    blackHand.possible(Card(Colour.Red, Number.five)) should be (true)
  }

  "The Card Drawing Logic" should "provide exactly 7 cards for a beginning hand" in {
    val emptyHand = new Hand(List.empty[Card])
    val fullHand = Draw.beginningHand(emptyHand)
    fullHand.count should be (7)
  }

  it should "never draw a +4 or choice card with a non-black colour" in {
    for (_ <- 1 to 50) {
      val card = Draw.draw()
      if (card.colour != Colour.Black) {
        card.value should not be (Number.plus4)
        card.value should not be (Number.choice)
      } else {
        Seq(Number.choice, Number.plus4) should contain (card.value)
      }
    }
  }

  "UnoLogic.playCard" should "handle normal cards (Deckt 'case _' ab)" in {
    val normalCard = Card(Colour.Red, Number.five)
    val state = GameState(
      playerHand = new Hand(List(normalCard)),
      cpuHand = new Hand(Nil),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val nextState = UnoLogic.playCard(state, normalCard)
    nextState.isPlayerTurn should be (false)
    nextState.statusMessage should be ("Du legst Red five")
  }

  it should "return error for invalid moves" in {
    val state = GameState(new Hand(List(Card(Colour.Blue, Number.zero))), new Hand(Nil), Card(Colour.Red, Number.five), Colour.Red, true)
    val nextState = UnoLogic.playCard(state, Card(Colour.Blue, Number.zero))
    nextState.statusMessage should be ("Ungültiger Zug!")
  }

  it should "execute plus2 logic" in {
    val plus2 = Card(Colour.Red, Number.plus2)
    val state = GameState(new Hand(List(plus2)), new Hand(List(Card(Colour.Blue, Number.one))), Card(Colour.Red, Number.five), Colour.Red, true)
    val nextState = UnoLogic.playCard(state, plus2)
    nextState.cpuHand.count should be (3)
    nextState.statusMessage should include ("CPU zieht 2!")
  }

  it should "execute plus4 logic and use chosen colour" in {
    val plus4 = Card(Colour.Black, Number.plus4)
    val state = GameState(new Hand(List(plus4)), new Hand(Nil), Card(Colour.Red, Number.five), Colour.Red, true)
    val nextState = UnoLogic.playCard(state, plus4, Some(Colour.Green))
    nextState.cpuHand.count should be (4)
    nextState.activeColour should be (Colour.Green)
  }

  it should "handle Skip/directionChange" in {
    val skip = Card(Colour.Red, Number.Skip)
    val state = GameState(new Hand(List(skip)), new Hand(Nil), Card(Colour.Red, Number.five), Colour.Red, true)
    val nextState = UnoLogic.playCard(state, skip)
    nextState.isPlayerTurn should be (true)
    nextState.statusMessage should include ("nochmal dran")
  }

  "UnoLogic.cpuTurn" should "handle CPU playing matching cards including specials" in {
    val cpuCards = List(
      Card(Colour.Red, Number.plus2),
      Card(Colour.Black, Number.plus4),
      Card(Colour.Red, Number.Skip)
    )

    cpuCards.foreach { card =>
      val state = GameState(new Hand(Nil), new Hand(List(card)), Card(Colour.Red, Number.zero), Colour.Red, false)
      val nextState = UnoLogic.cpuTurn(state)
      nextState.pile should be (card)
    }
  }

  it should "use cpuWish when playing black cards" in {
    val black = Card(Colour.Black, Number.choice)
    val wishColour = Card(Colour.Green, Number.five)

    val state = GameState(new Hand(Nil), new Hand(List(black, wishColour)), Card(Colour.Red, Number.zero), Colour.Red, false)
    val nextState = UnoLogic.cpuTurn(state)
    nextState.activeColour should be (Colour.Green)
  }

  it should "force CPU to draw if no card matches" in {
    val state = GameState(new Hand(Nil), new Hand(List(Card(Colour.Blue, Number.zero))), Card(Colour.Red, Number.five), Colour.Red, false)
    val nextState = UnoLogic.cpuTurn(state)
    nextState.cpuHand.count should be (2)
    nextState.statusMessage should be ("CPU zieht.")
  }

  "UnoLogic.drawCard" should "add card to correct hand based on turn" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.one), Colour.Red, true)

    val pDraw = UnoLogic.drawCard(state)
    pDraw.playerHand.count should be (1)
    pDraw.isPlayerTurn should be (false)

    val cDraw = UnoLogic.drawCard(state.copy(isPlayerTurn = false))
    cDraw.cpuHand.count should be (1)
    cDraw.isPlayerTurn should be (true)
  }
}