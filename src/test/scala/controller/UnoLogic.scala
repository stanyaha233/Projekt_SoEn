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
    logicNormal.state.statusMessage should be("Du legst Red five")
    logicNormal.state.activeColour should be(Colour.Red)

    val logicP2 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    logicP2.playCard(Card(Colour.Red, Number.plus2))
    logicP2.state.cpuHand.count should be(2)
    logicP2.state.isPlayerTurn should be(false)
    logicP2.state.statusMessage should be("Du legst Red plus2. CPU zieht 2!")

    val logicP4 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    logicP4.playCard(Card(Colour.Black, Number.plus4), Some(Colour.Blue))
    logicP4.state.cpuHand.count should be(4)
    logicP4.state.activeColour should be(Colour.Blue)
    logicP4.state.isPlayerTurn should be(false)
    logicP4.state.statusMessage should be("Du legst Black plus4. CPU zieht 4!")

    val logicSkip = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.skip)))))
    logicSkip.playCard(Card(Colour.Red, Number.skip))
    logicSkip.state.isPlayerTurn should be(true)
    logicSkip.state.statusMessage should be("Du legst Red skip. Du bist nochmal dran!")

    val logicDir = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.directionchange)))))
    logicDir.playCard(Card(Colour.Red, Number.directionchange))
    logicDir.state.isPlayerTurn should be(true)
    logicDir.state.statusMessage should be("Du legst Red directionchange. Du bist nochmal dran!")

    val logicChoice = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.choice)))))
    logicChoice.playCard(Card(Colour.Black, Number.choice), None)
    logicChoice.state.activeColour should be(Colour.Red)
    logicChoice.state.statusMessage should be("Du legst Black choice")

    val logicSameValue = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Blue, Number.zero)))))
    logicSameValue.playCard(Card(Colour.Blue, Number.zero))
    logicSameValue.state.isPlayerTurn should be(false)

    val logicInvalid = new UnoLogic(baseState)
    logicInvalid.playCard(Card(Colour.Blue, Number.nine))
    logicInvalid.state.statusMessage should be("Ungültiger Zug!")

    val logicNotOwned = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.one)))))
    logicNotOwned.playCard(Card(Colour.Red, Number.five))
  }

  "UnoLogic.cpuTurn" should "handle all CPU cases" in {
    val baseState = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, false)

    val logicNormal = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.five)))))
    logicNormal.cpuTurn()
    logicNormal.state.pile.value should be(Number.five)
    logicNormal.state.isPlayerTurn should be(true)
    logicNormal.state.statusMessage should be("Gegner legt Red five")

    val logicP2 = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    logicP2.cpuTurn()
    logicP2.state.playerHand.count should be(2)
    logicP2.state.isPlayerTurn should be(true)

    val logicP4 = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    logicP4.cpuTurn()
    logicP4.state.playerHand.count should be(4)
    logicP4.state.isPlayerTurn should be(true)

    val logicSkip = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.skip)))))
    logicSkip.cpuTurn()
    logicSkip.state.isPlayerTurn should be(false)

    val logicDir = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Red, Number.directionchange)))))
    logicDir.cpuTurn()
    logicDir.state.isPlayerTurn should be(false)

    val logicDraw = new UnoLogic(baseState.copy(cpuHand = new Hand(List(Card(Colour.Blue, Number.nine)))))
    logicDraw.cpuTurn()
    logicDraw.state.cpuHand.count should be(2)
    logicDraw.state.isPlayerTurn should be(true)

    val logicEmpty = new UnoLogic(baseState.copy(cpuHand = new Hand(Nil)))
    logicEmpty.cpuTurn()
    logicEmpty.state.cpuHand.count should be(1)
    logicEmpty.state.isPlayerTurn should be(true)

    val cpuWishState = GameState(new Hand(Nil), new Hand(List(Card(Colour.Green, Number.one), Card(Colour.Black, Number.choice))), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logicWish = new UnoLogic(cpuWishState)
    logicWish.cpuTurn()
    logicWish.state.activeColour should be(Colour.Green)
    logicWish.state.isPlayerTurn should be(true)
  }

  it should "handle cpuWish logic branches" in {
    val black = Card(Colour.Black, Number.choice)
    val state1 = GameState(new Hand(Nil), new Hand(List(black)), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logic1 = new UnoLogic(state1)
    logic1.cpuTurn()
    Colour.values should contain(logic1.state.activeColour)
    logic1.state.isPlayerTurn should be(true)

    val state2 = GameState(new Hand(Nil), new Hand(List(black, Card(Colour.Green, Number.one))), Card(Colour.Red, Number.zero), Colour.Red, false)
    val logic2 = new UnoLogic(state2)
    logic2.cpuTurn()
    Colour.values should contain(logic2.state.activeColour)
    logic2.state.isPlayerTurn should be(true)
  }

  "UnoLogic.drawCard" should "handle player and cpu drawing" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.one), Colour.Red, true)
    
    val logicP = new UnoLogic(state)
    logicP.drawCard()
    logicP.state.playerHand.count should be(1)
    logicP.state.isPlayerTurn should be(false)
    logicP.state.statusMessage should be("Gezogen.")

    val logicC = new UnoLogic(state.copy(isPlayerTurn = false))
    logicC.drawCard()
    logicC.state.cpuHand.count should be(1)
    logicC.state.isPlayerTurn should be(true)
    logicC.state.statusMessage should be("CPU zieht.")
  }

  "Card" should "be correctly instantiated" in {
    val card = Card(Colour.Yellow, Number.skip)
    card.colour should be(Colour.Yellow)
    card.value should be(Number.skip)
  }
  "UnoLogic.executePlaceCard" should "abdecken den Default-Zweig (Standardkarte)" in {
    val baseState = GameState(new Hand(List(Card(Colour.Red, Number.five))), Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val logic = new UnoLogic(baseState)
    logic.executePlaceCard(Card(Colour.Red, Number.five))
    logic.state.playerHand.count should be(0) // Karte sollte weg sein
  }
}