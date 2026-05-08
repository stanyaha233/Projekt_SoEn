package uno.aview

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._
import uno.controller.UnoLogic

class UnoPlaySpec extends AnyFlatSpec with Matchers {

  "UnoPlay.parseInput" should "correctly transform string input into a GameState update via UnoLogic" in {
    val myCard = Card(Colour.Red, Number.five)
    val extraCard = Card(Colour.Blue, Number.one) // Verhindert sys.exit(0) im Test
    val state = GameState(
      playerHand = new Hand(List(myCard, extraCard)),
      cpuHand = new Hand(List(extraCard)),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val controller = new UnoLogic(state)
    val tui = new UnoPlay(controller)

    tui.parseInput("red five")
    controller.state.playerHand.count should be(1)
    controller.state.pile should be(myCard)
  }

  it should "return an error message for nonsense input" in {
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(new Hand(List(extraCard)), new Hand(List(extraCard)), Card(Colour.Red, Number.zero), Colour.Red, true)
    val controller = new UnoLogic(state)
    val tui = new UnoPlay(controller)
    
    tui.parseInput("hallo welt")
    controller.state.statusMessage should include("Eingabe falsch")
  }

  it should "return an error message for incomplete input" in {
    val extraCard = Card(Colour.Blue, Number.one)
    val state = GameState(new Hand(List(extraCard)), new Hand(List(extraCard)), Card(Colour.Red, Number.zero), Colour.Red, true)
    val controller = new UnoLogic(state)
    val tui = new UnoPlay(controller)
    
    tui.parseInput("red")
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
    val controller = new UnoLogic(state)
    val tui = new UnoPlay(controller)
    
    tui.parseInput("red seven")
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
    val controller = new UnoLogic(state)
    val tui = new UnoPlay(controller)
    
    val in = new java.io.ByteArrayInputStream("blue\n".getBytes)
    Console.withIn(in) {
      tui.parseInput("black choice")
    }
    controller.state.activeColour should be(Colour.Blue)
  }
}