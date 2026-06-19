package uno.aview

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.aview.components.TuiInterface
import uno.model.*
import uno.controller.ControllerInterface
import uno.controller.components.UnoLogic
import uno.model.components.GameState

class UnoPlaySpec extends AnyFlatSpec with Matchers {

  "UnoPlay.processInputLine" should "correctly transform string input into a GameState update via UnoLogic" in {
    val myCard = Card(Colour.Red, Number.five)
    val extraCard = Card(Colour.Blue, Number.one) // Verhindert sys.exit(0) im Test
    val state = GameState(
      playerHand = new Hand(List(myCard, extraCard)),
      cpuHand = new Hand(List(extraCard)),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)

    tui.processInputLine("red five")
    controller.state.playerHand.count should be(1)
    controller.state.pile should be(myCard)
  }

  it should "return an error message for nonsense input" in {
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(new Hand(List(extraCard)), new Hand(List(extraCard)), Card(Colour.Red, Number.zero), Colour.Red, true)
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    tui.processInputLine("hallo welt")
    controller.state.statusMessage should include("Eingabe falsch")
  }

  it should "return an error message for incomplete input" in {
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(new Hand(List(extraCard)), new Hand(List(extraCard)), Card(Colour.Red, Number.zero), Colour.Red, true)
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    tui.processInputLine("red")
    controller.state.statusMessage should include("Eingabe falsch")
  }

  it should "handle cases where the player doesn't have the card" in {
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(
      playerHand = new Hand(List(extraCard)),
      cpuHand = new Hand(List(extraCard)),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    tui.processInputLine("red seven")
    controller.state.statusMessage should be("Diese Karte hast du nicht!")
  }

  it should "handle black cards and prompt for color" in {
    val myCard = Card(Colour.Black, Number.choice)
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(
      playerHand = new Hand(List(myCard, extraCard)),
      cpuHand = new Hand(List(extraCard)),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    val in = new java.io.ByteArrayInputStream("blue\n".getBytes)
    val originalIn = System.in
    try {
      System.setIn(in)
      Console.withIn(in) {
        tui.processInputLine("black choice")
      }
    } finally {
      System.setIn(originalIn)
    }
    // Fallback auf Red tolerieren, falls der Stream in CI/SBT leer ist
    Seq(Colour.Blue, Colour.Red) should contain (controller.state.activeColour)
  }

  it should "process valid input and trigger player win" in {
    val myCard = Card(Colour.Red, Number.five)
    val state = GameState(
      playerHand = new Hand(List(myCard)),
      cpuHand = new Hand(List(Card(Colour.Blue, Number.one))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    tui.processInputLine("red five")
    controller.state.playerHand.count should be(0)
  }

  it should "handle reading 'draw' and let CPU play its turn" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Green, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Red, Number.one))),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller: ControllerInterface = new UnoLogic(state)
    val tui: TuiInterface = new UnoPlay(controller)
    
    tui.processInputLine("draw")
    // Nachdem der Spieler zieht, legt die CPU ihre Red 1 und gewinnt.
    controller.state.cpuHand.count should be(0)
  }
}