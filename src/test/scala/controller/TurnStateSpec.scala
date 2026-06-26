package uno.controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model.*
import uno.controller.components.UnoLogic
import uno.model.components.GameState

class TurnStateSpec extends AnyFlatSpec with Matchers {
  "PlayerTurnState" should "allow playing a valid card and transition to CpuTurnState if the turn changes" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Red, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val logic = new UnoLogic(state) {
      override def cpuTurn(): Unit = ()
    }
    logic.turnState should be(PlayerTurnState)

    PlayerTurnState.playCard(logic, Card(Colour.Red, Number.five))
    logic.turnState should be(CpuTurnState)
  }

  it should "not transition to CpuTurnState if an invalid card is played" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val logic = new UnoLogic(state)
    PlayerTurnState.playCard(logic, Card(Colour.Green, Number.five))
    logic.turnState should be(PlayerTurnState)
    logic.state.statusMessage should be("Ungültiger Zug!")
  }

  it should "draw a card and transition to CpuTurnState" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val logic = new UnoLogic(state) {
      override def cpuTurn(): Unit = ()
    }
    PlayerTurnState.drawCard(logic)
    logic.state.playerHand.count should be(2)
    logic.turnState should be(CpuTurnState)
  }

  it should "do nothing on cpuTurn" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val logic = new UnoLogic(state)
    PlayerTurnState.cpuTurn(logic)
    logic.turnState should be(PlayerTurnState)
  }

  "CpuTurnState" should "do nothing on playCard" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = false
    )
    val logic = new UnoLogic(state)
    logic.turnState should be(CpuTurnState)
    CpuTurnState.playCard(logic, Card(Colour.Green, Number.five))
    logic.turnState should be(CpuTurnState)
  }

  it should "draw a card for CPU and transition to PlayerTurnState" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = false
    )
    val logic = new UnoLogic(state)
    CpuTurnState.drawCard(logic)
    logic.state.cpuHand.count should be(2)
    logic.turnState should be(PlayerTurnState)
  }

  it should "play a card for CPU and transition to PlayerTurnState" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Red, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = false
    )
    val logic = new UnoLogic(state)
    CpuTurnState.cpuTurn(logic)
    logic.state.pile should be(Card(Colour.Red, Number.nine))
    logic.turnState should be(PlayerTurnState)
  }

  it should "draw a card for CPU if CPU cannot play, and transition to PlayerTurnState" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = false
    )
    val logic = new UnoLogic(state)
    CpuTurnState.cpuTurn(logic)
    logic.state.cpuHand.count should be(2)
    logic.turnState should be(PlayerTurnState)
  }

  it should "handle deck empty failure on drawCard for Player" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val logic = new UnoLogic(state)
    try {
      Draw.setDeck(null)
      PlayerTurnState.drawCard(logic)
      logic.state.statusMessage should be("Stapel ist leer!")
    } finally {
      Draw.setDeck(DeckFactory.createStandardDeck())
    }
  }

  it should "handle deck empty failure on drawCard for CPU" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.nine))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = false
    )
    val logic = new UnoLogic(state)
    try {
      Draw.setDeck(null)
      CpuTurnState.drawCard(logic)
      logic.state.statusMessage should be("Stapel ist leer!")
    } finally {
      Draw.setDeck(DeckFactory.createStandardDeck())
    }
  }

  it should "invoke default arguments on playCard trait definition via reflection on an anonymous TurnState" in {
    val anonymousState = new TurnState {
      override def playCard(controller: UnoLogic, card: Card, chosenColour: Option[Colour.Value]): Unit = {}
      override def drawCard(controller: UnoLogic): Unit = {}
      override def cpuTurn(controller: UnoLogic): Unit = {}
    }
    val method = classOf[TurnState].getMethod("playCard$default$3")
    val defaultVal = method.invoke(anonymousState).asInstanceOf[Option[Colour.Value]]
    defaultVal should be(None)
  }
}
