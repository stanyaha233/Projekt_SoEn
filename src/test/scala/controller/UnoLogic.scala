package uno.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class UnoLogicSpec extends AnyFlatSpec with Matchers {

  "A Hand" should "be initialized and add cards correctly" in {
    val hand = new Hand(List(Card(Colour.Red, Number.one)))
    hand.count should be(1)
    val newHand = hand.add(Card(Colour.Blue, Number.two))
    newHand.count should be(2)
  }

  it should "check if a move is possible" in {
    val hand = new Hand(List(Card(Colour.Red, Number.nine)))
    hand.possible(Card(Colour.Red, Number.five)) should be(true)
    hand.possible(Card(Colour.Blue, Number.five)) should be(false)
    val blackHand = new Hand(List(Card(Colour.Black, Number.choice)))
    blackHand.possible(Card(Colour.Red, Number.five)) should be(true)
  }

  "The Draw Logic" should "provide 7 cards for beginning hand" in {
    Draw.beginningHand(new Hand(Nil)).count should be(7)
  }

  it should "verify draw constraints" in {
    for (_ <- 1 to 50) {
      val card = Draw.draw()
      if (card.colour != Colour.Black) {
        card.value should not be(Number.plus4)
        card.value should not be(Number.choice)
      } else {
        Seq(Number.choice, Number.plus4) should contain(card.value)
      }
    }
  }

  "UnoLogic.playCard" should "handle all card types for coverage" in {
    val baseState = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)

    val logicNormal = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.five)))))
    logicNormal.playCard(Card(Colour.Red, Number.five))
    logicNormal.state.isPlayerTurn should be(false)

    val logicP2 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    logicP2.playCard(Card(Colour.Red, Number.plus2))
    logicP2.state.cpuHand.count should be(2)

    val logicP4 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    logicP4.playCard(Card(Colour.Black, Number.plus4), Some(Colour.Blue))
    logicP4.state.cpuHand.count should be(4)
    logicP4.state.activeColour should be(Colour.Blue)

    val logicSkip = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.skip)))))
    logicSkip.playCard(Card(Colour.Red, Number.skip))
    logicSkip.state.isPlayerTurn should be(true)

    val logicDir = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.directionchange)))))
    logicDir.playCard(Card(Colour.Red, Number.directionchange))
    logicDir.state.isPlayerTurn should be(true)

    val logicChoice = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.choice)))))
    logicChoice.playCard(Card(Colour.Black, Number.choice), None)
    logicChoice.state.activeColour should be(Colour.Red)

    val logicInvalid = new UnoLogic(baseState)
    logicInvalid.playCard(Card(Colour.Blue, Number.nine))
    logicInvalid.state.statusMessage should be("Ungültiger Zug!")
  }

  "UnoLogic.cpuTurn" should "handle all CPU cases" in {
    val baseState = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, false)

    val logicNormal = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.five)))))
    logicNormal.cpuTurn()
    logicNormal.state.pile.value should be(Number.five)

    val logicP2 = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    logicP2.cpuTurn()
    logicP2.state.playerHand.count should be(2)

    val logicP4 = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    logicP4.cpuTurn()
    logicP4.state.playerHand.count should be(4)

    val logicSkip = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.skip)))))
    logicSkip.cpuTurn()
    logicSkip.state.isPlayerTurn should be(false)

    val logicDir = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.directionchange)))))
    logicDir.cpuTurn()
    logicDir.state.isPlayerTurn should be(false)

    val logicDraw = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Blue, Number.nine)))))
    logicDraw.cpuTurn()
    logicDraw.state.cpuHand.count should be(2)

    val cpuWishState = GameState(new Hand(Nil), new Hand(List(Card(Colour.Green, Number.one), Card(Colour.Black, Number.choice))), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logicWish = new UnoLogic(cpuWishState)
    logicWish.cpuTurn()
    logicWish.state.activeColour should be(Colour.Green)
  }

  it should "handle cpuWish logic branches" in {
    val black = Card(Colour.Black, Number.choice)
    val state1 = GameState(new Hand(Nil), new Hand(List(black)), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logic1 = new UnoLogic(state1)
    logic1.cpuTurn()
    Colour.values should contain(logic1.state.activeColour)

    val state2 = GameState(new Hand(Nil), new Hand(List(black, Card(Colour.Green, Number.one))), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logic2 = new UnoLogic(state2)
    logic2.cpuTurn()
    Colour.values should contain(logic2.state.activeColour)
  }

  "UnoLogic.drawCard" should "handle player and cpu drawing" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.one), Colour.Red, true)
    
    val logicP = new UnoLogic(state)
    logicP.drawCard()
    logicP.state.playerHand.count should be(1)

    val logicC = new UnoLogic(state.copy(isPlayerTurn = false))
    logicC.drawCard()
    logicC.state.cpuHand.count should be(1)
  }

  "Card" should "be correctly instantiated" in {
    val card = Card(Colour.Yellow, Number.skip)
    card.colour should be(Colour.Yellow)
    card.value should be(Number.skip)
  }
}