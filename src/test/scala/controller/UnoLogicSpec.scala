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
    val baseState = GameState(new Hand(Nil), new Hand(List(Card(Colour.Blue, Number.nine))), Card(Colour.Red, Number.zero), Colour.Red, true)

    val canPlayLogic = new UnoLogic(baseState)
    canPlayLogic.canPlay(Card(Colour.Green, Number.one)) should be(false)

    val logicNormal = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.five)))))
    logicNormal.playCard(Card(Colour.Red, Number.five))
    logicNormal.state.playerHand.count should be(0)
    Seq(1, 2) should contain (logicNormal.state.cpuHand.count) // CPU zieht nach, legt aber eventuell direkt

    val directNormalLogic = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.five)))))
    directNormalLogic.executePlaceCard(Card(Colour.Red, Number.five))
    directNormalLogic.state.statusMessage should be("Du legst Red five")

    val logicP2 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.plus2)))))
    logicP2.playCard(Card(Colour.Red, Number.plus2))

    val logicP4 = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.plus4)))))
    logicP4.playCard(Card(Colour.Black, Number.plus4), Some(Colour.Blue))
    logicP4.state.activeColour should be(Colour.Blue)

    val logicSkip = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.skip)))))
    logicSkip.playCard(Card(Colour.Red, Number.skip))
    logicSkip.state.playerHand.count should be(0)

    val logicDir = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.directionchange)))))
    logicDir.playCard(Card(Colour.Red, Number.directionchange))
    logicDir.state.playerHand.count should be(0)

    val logicChoice = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Black, Number.choice)))))
    logicChoice.playCard(Card(Colour.Black, Number.choice), None)
    logicChoice.state.activeColour should be(Colour.Red)

    val logicSameValue = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Blue, Number.zero)))))
    logicSameValue.playCard(Card(Colour.Blue, Number.zero))
    logicSameValue.state.cpuHand.count should be(0) // CPU legt Blue 9 auf Blue 0

    val logicInvalid = new UnoLogic(baseState)
    logicInvalid.playCard(Card(Colour.Blue, Number.nine))
    logicInvalid.state.statusMessage should be("Ungültiger Zug!")

    val logicNotOwned = new UnoLogic(baseState.copy(playerHand = new Hand(List(Card(Colour.Red, Number.one)))))
    logicNotOwned.playCard(Card(Colour.Red, Number.five))
  }

  it should "update the status message for special playCard branches" in {
    val plus2State = GameState(new Hand(List(Card(Colour.Red, Number.plus2))), new Hand(List(Card(Colour.Blue, Number.nine))), Card(Colour.Red, Number.zero), Colour.Red, true)
    val plus2Logic = new UnoLogic(plus2State)
    plus2Logic.executePlaceCard(Card(Colour.Red, Number.plus2))
    plus2Logic.state.statusMessage should be("Du legst Red plus2. CPU zieht 2!")
    plus2Logic.state.isPlayerTurn should be(false)
    plus2Logic.state.cpuHand.count should be(3)

    val plus4State = GameState(new Hand(List(Card(Colour.Black, Number.plus4))), new Hand(List(Card(Colour.Blue, Number.nine))), Card(Colour.Red, Number.zero), Colour.Red, true)
    val plus4Logic = new UnoLogic(plus4State)
    plus4Logic.executePlaceCard(Card(Colour.Black, Number.plus4), Some(Colour.Blue))
    plus4Logic.state.statusMessage should be("Du legst Black plus4. CPU zieht 4!")
    plus4Logic.state.activeColour should be(Colour.Blue)
    plus4Logic.state.isPlayerTurn should be(false)
    plus4Logic.state.cpuHand.count should be(5)

    val skipState = GameState(new Hand(List(Card(Colour.Red, Number.skip))), new Hand(List(Card(Colour.Blue, Number.nine))), Card(Colour.Red, Number.zero), Colour.Red, true)
    val skipLogic = new UnoLogic(skipState)
    skipLogic.executePlaceCard(Card(Colour.Red, Number.skip))
    skipLogic.state.statusMessage should be("Du legst Red skip. Du bist nochmal dran!")
    skipLogic.state.isPlayerTurn should be(true)
  }

  it should "detect playable and unplayable cards via canPlay" in {
    val state = GameState(new Hand(List(Card(Colour.Red, Number.one))), new Hand(List(Card(Colour.Blue, Number.two))), Card(Colour.Red, Number.zero), Colour.Red, true)
    val logic = new UnoLogic(state)

    logic.canPlay(Card(Colour.Black, Number.choice)) should be(true)
    logic.canPlay(Card(Colour.Green, Number.one)) should be(false)
  }

  it should "reject invalid executePlaceCard moves without changing the pile" in {
    val invalidState = GameState(new Hand(List(Card(Colour.Green, Number.one))), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Blue, true)
    val logic = new UnoLogic(invalidState)

    logic.executePlaceCard(Card(Colour.Green, Number.one))

    logic.state.playerHand should be(invalidState.playerHand)
    logic.state.pile should be(invalidState.pile)
    logic.state.statusMessage should be("Ungültiger Zug!")
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

    val cpuColourState = GameState(
      new Hand(Nil),
      new Hand(List(Card(Colour.Red, Number.five), Card(Colour.Black, Number.choice), Card(Colour.Green, Number.one))),
      Card(Colour.Red, Number.zero),
      Colour.Red,
      false
    )
    val logicColour = new UnoLogic(cpuColourState)
    logicColour.cpuTurn()
    logicColour.state.pile should be(Card(Colour.Red, Number.five))
    logicColour.state.activeColour should be(Colour.Red)

    val cpuColourFallbackState = GameState(
      new Hand(Nil),
      new Hand(List(Card(Colour.Green, Number.one), Card(Colour.Red, Number.five))),
      Card(Colour.Red, Number.zero),
      Colour.Red,
      false
    )
    val logicColourFallback = new UnoLogic(cpuColourFallbackState)
    logicColourFallback.cpuTurn()
    logicColourFallback.state.pile should be(Card(Colour.Red, Number.five))
    logicColourFallback.state.activeColour should be(Colour.Red)
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
    val state = GameState(new Hand(List(Card(Colour.Green, Number.five))), new Hand(List(Card(Colour.Blue, Number.nine))), Card(Colour.Red, Number.one), Colour.Red, true)
    
    val logicP = new UnoLogic(state) {
      override def cpuTurn(): Unit = ()
    }
    logicP.drawCard()
    logicP.state.playerHand.count should be(2)
    Seq(1, 2) should contain (logicP.state.cpuHand.count) // CPU kann ziehen oder legen
    logicP.state.isPlayerTurn should be(false)
    logicP.state.statusMessage should be("Karte gezogen. Gegner ist am Zug.")

    val logicC = new UnoLogic(state.copy(isPlayerTurn = false))
    logicC.drawCard()
    logicC.state.cpuHand.count should be(2)
    logicC.state.isPlayerTurn should be(true)
    logicC.state.statusMessage should be("CPU hat gezogen.")
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

  "UnoLogic" should "sort hands via sort functions and update status" in {
    val state = GameState(new Hand(List(Card(Colour.Blue, Number.two), Card(Colour.Red, Number.one))), new Hand(Nil), Card(Colour.Red, Number.one), Colour.Red, true)
    val logic = new UnoLogic(state)

    logic.sortHandByColor()
    logic.state.statusMessage should be("Karten wurden nach Farbe sortiert.")

    logic.sortHandByValue()
    logic.state.statusMessage should be("Karten wurden nach Zahl sortiert.")
  }

  it should "handle undo" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Blue, Number.two), Colour.Blue, true)
    val logic = new UnoLogic(state)
    logic.undo()
    // Undo on empty stack should just notify observers but not crash
    logic.state should be(state)
  }

  it should "handle drawCard failure when pile throws exception safely" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Blue, Number.two), Colour.Blue, true)
    val logic = new UnoLogic(state)
    
    try {
      Draw.setDeck(null) // Provoziere absichtlich einen Fehler (NPE) für den Failure-Zweig
      logic.drawCard()
      logic.state.statusMessage should be("Stapel ist leer!")
    } finally {
      Draw.setDeck(DeckFactory.createStandardDeck()) // Stapel immer sicher mit frischen Karten wiederherstellen!
    }
  }

  "Hand.sortCards" should "sort cards using given strategies directly on the hand" in {
    val hand = new Hand(List(Card(Colour.Blue, Number.two), Card(Colour.Red, Number.one)))
    val sortedHand = hand.sortCards(new SortByColorStrategy())
    sortedHand.cards should be(List(Card(Colour.Red, Number.one), Card(Colour.Blue, Number.two)))
  }

  "BotStrategies" should "correctly choose cards based on their logic" in {
    val firstStrategy = new FirstPossibleStrategy()
    val blackStrategy = new BlackFirstStrategy()
    val hand1 = new Hand(List(Card(Colour.Green, Number.nine), Card(Colour.Blue, Number.two)))
    val handWithBlack = new Hand(List(Card(Colour.Blue, Number.two), Card(Colour.Black, Number.choice)))

    firstStrategy.chooseCard(hand1, Colour.Blue, Number.one) should be(Some(Card(Colour.Blue, Number.two)))
    firstStrategy.chooseCard(hand1, Colour.Red, Number.five) should be(None)

    blackStrategy.chooseCard(handWithBlack, Colour.Blue, Number.one) should be(Some(Card(Colour.Black, Number.choice)))
    blackStrategy.chooseCard(hand1, Colour.Blue, Number.one) should be(Some(Card(Colour.Blue, Number.two)))
    blackStrategy.chooseCard(hand1, Colour.Red, Number.five) should be(None)
  }
}