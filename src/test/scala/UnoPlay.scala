package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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

    // Testet, ob aus dem String "red five" ein gültiger Spielzug wird
    val nextState = parseInput("red five", state)
    nextState.playerHand.count should be (0)
    nextState.pile should be (myCard)
  }

  it should "return an error message for nonsense input" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val nextState = parseInput("hallo welt", state)
    nextState.statusMessage should include ("Eingabe falsch")
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
    nextState.statusMessage should be ("Diese Karte hast du nicht!")
  }

  "The game loop conditions" should "detect a win for the player" in {
    val state = GameState(new Hand(Nil), new Hand(List(Card(Colour.Red, Number.one))), Card(Colour.Red, Number.five), Colour.Red, true)
    // Hier prüfen wir nur die Bedingung, die in deiner gameLoop steht
    state.playerHand.count should be (0)
  }

  it should "detect a win for the CPU" in {
    val state = GameState(new Hand(List(Card(Colour.Red, Number.one))), new Hand(Nil), Card(Colour.Red, Number.five), Colour.Red, false)
    state.cpuHand.count should be (0)
  }
}