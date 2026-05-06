package uno.view

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class MainSpec extends AnyFlatSpec with Matchers {

  "parseInput" should "correctly transform string input into a GameState" in {
    val myCard = Card(Colour.Red, Number.five)
    val state = GameState(
      playerHand = new Hand(List(myCard)),
      cpuHand = new Hand(Nil),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )

    val nextState = parseInput("red five", state)
    nextState.playerHand.count should be(0)
    nextState.pile should be(myCard)
  }

  it should "return an error message for nonsense input" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val nextState = parseInput("hallo welt", state)
    nextState.statusMessage should include("Eingabe falsch")
  }

  it should "return an error message for incomplete input" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val nextState = parseInput("red", state)
    nextState.statusMessage should include("Eingabe falsch")
  }

  it should "handle cases where the player doesn't have the card" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Blue, Number.one))),
      cpuHand = new Hand(Nil),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val nextState = parseInput("red seven", state)
    nextState.statusMessage should be("Diese Karte hast du nicht!")
  }

  it should "handle black cards and prompt for color" in {
    val myCard = Card(Colour.Black, Number.choice)
    val state = GameState(
      playerHand = new Hand(List(myCard)),
      cpuHand = new Hand(Nil),
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val in = new java.io.ByteArrayInputStream("blue\n".getBytes)
    val nextState = Console.withIn(in) {
      parseInput("black choice", state)
    }
    nextState.activeColour should be(Colour.Blue)
  }

  "The game loop conditions" should "detect a win for the player" in {
    val state = GameState(new Hand(Nil), new Hand(List(Card(Colour.Red, Number.one))), Card(Colour.Red, Number.five), Colour.Red, true)
    gameLoop(state)
    state.playerHand.count should be(0)
  }

  it should "detect a win for the CPU" in {
    val state = GameState(new Hand(List(Card(Colour.Red, Number.one))), new Hand(Nil), Card(Colour.Red, Number.five), Colour.Red, false)
    gameLoop(state)
    state.cpuHand.count should be(0)
  }

  it should "handle draw input within game loop when not possible" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Blue, Number.one))),
      cpuHand = new Hand(List(Card(Colour.Red, Number.two))), 
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val in = new java.io.ByteArrayInputStream("draw\n".getBytes)
    Console.withIn(in) { gameLoop(state) }
  }

  it should "handle normal play input within game loop" in {
    val state = GameState(
      playerHand = new Hand(List(Card(Colour.Red, Number.five))),
      cpuHand = new Hand(List(Card(Colour.Red, Number.two))), 
      pile = Card(Colour.Red, Number.zero),
      activeColour = Colour.Red,
      isPlayerTurn = true
    )
    val in = new java.io.ByteArrayInputStream("red five\n".getBytes)
    Console.withIn(in) { gameLoop(state) }
  }
}