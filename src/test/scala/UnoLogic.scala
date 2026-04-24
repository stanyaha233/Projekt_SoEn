package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnoSpec extends AnyFlatSpec with Matchers {

  "A Hand" should "be initialized and add cards correctly" in {
    val hand = new Hand(List(Card(Colour.Red, Number.one)))
    hand.count should be (1)
    val newHand = hand.add(Card(Colour.Blue, Number.two))
    newHand.count should be (2)
  }

  it should "check if a move is possible" in {
    val hand = new Hand(List(Card(Colour.Red, Number.nine)))
    hand.possible(Card(Colour.Red, Number.five)) should be (true)
    hand.possible(Card(Colour.Blue, Number.five)) should be (false)
    val blackHand = new Hand(List(Card(Colour.Black, Number.choice)))
    blackHand.possible(Card(Colour.Red, Number.five)) should be (true)
  }

  "The Draw Logic" should "provide 7 cards for beginning hand" in {
    Draw.beginningHand(new Hand(Nil)).count should be (7)
  }

  it should "verify draw constraints" in {
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

  "UnoLogic.playCard" should "handle all card types for coverage" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)

    val sNormal = UnoLogic.playCard(state.copy(playerHand = new Hand(List(Card(Colour.Red, Number.five)))), Card(Colour.Red, Number.five))
    sNormal.isPlayerTurn should be (false)

    val sP2 = UnoLogic.playCard(state.copy(playerHand = new Hand(List(Card(Colour.Red, Number.plus2)))), Card(Colour.Red, Number.plus2))
    sP2.cpuHand.count should be (2)

    val sP4 = UnoLogic.playCard(state.copy(playerHand = new Hand(List(Card(Colour.Black, Number.plus4)))), Card(Colour.Black, Number.plus4), Some(Colour.Blue))
    sP4.cpuHand.count should be (4)

    val sSkip = UnoLogic.playCard(state.copy(playerHand = new Hand(List(Card(Colour.Red, Number.Skip)))), Card(Colour.Red, Number.Skip))
    sSkip.isPlayerTurn should be (true)

    val sInvalid = UnoLogic.playCard(state, Card(Colour.Blue, Number.nine))
    sInvalid.statusMessage should be ("Ungültiger Zug!")
  }

  "UnoLogic.cpuTurn" should "handle all CPU cases" in {
    val baseState = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, false)

    val sNormal = UnoLogic.cpuTurn(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.five)))))
    sNormal.pile.value should be (Number.five)

    val sP2 = UnoLogic.cpuTurn(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    sP2.playerHand.count should be (2)

    val sP4 = UnoLogic.cpuTurn(baseState.copy(cpuHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    sP4.playerHand.count should be (4)

    val sSkip = UnoLogic.cpuTurn(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.Skip)))))
    sSkip.isPlayerTurn should be (false)

    val sDraw = UnoLogic.cpuTurn(baseState.copy(cpuHand = new Hand(List(Card(Colour.Blue, Number.nine)))))
    sDraw.cpuHand.count should be (2)
  }

  it should "handle cpuWish logic branches" in {
    val black = Card(Colour.Black, Number.choice)
    val state1 = GameState(new Hand(Nil), new Hand(List(black)), Card(Colour.Red, Number.zero), Colour.Red, false)
    Colour.values should contain (UnoLogic.cpuTurn(state1).activeColour)

    val state2 = GameState(new Hand(Nil), new Hand(List(black, Card(Colour.Green, Number.one))), Card(Colour.Red, Number.zero), Colour.Red, false)
    Colour.values should contain (UnoLogic.cpuTurn(state2).activeColour)
  }

  "UnoLogic.drawCard" should "handle player and cpu drawing" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.one), Colour.Red, true)
    UnoLogic.drawCard(state).playerHand.count should be (1)
    UnoLogic.drawCard(state.copy(isPlayerTurn = false)).cpuHand.count should be (1)
  }

  "Card" should "be correctly instantiated" in {
    val card = Card(Colour.Yellow, Number.Skip)
    card.colour should be (Colour.Yellow)
    card.value should be (Number.Skip)
  }
}