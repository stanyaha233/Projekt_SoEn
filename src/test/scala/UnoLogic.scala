package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UnoLogicSpec extends AnyFlatSpec with Matchers {

  // --- 1. BASIC RULES (canPlay) ---
  "UnoLogic.canPlay" should "allow playing cards of the same color or value" in {
    val top = Card(Colour.Red, Number.five)
    val state = GameState(new Hand(Nil), new Hand(Nil), top, Colour.Red, true)

    UnoLogic.canPlay(Card(Colour.Red, Number.seven), state) shouldBe true
    UnoLogic.canPlay(Card(Colour.Blue, Number.five), state) shouldBe true
    UnoLogic.canPlay(Card(Colour.Black, Number.plus4), state) shouldBe true
    UnoLogic.canPlay(Card(Colour.Blue, Number.zero), state) shouldBe false
  }

  // --- 2. PLAYER ACTIONS (playCard) ---
  "UnoLogic.playCard" should "correctly update state for normal cards" in {
    val card = Card(Colour.Red, Number.one)
    val state = GameState(new Hand(List(card)), new Hand(Nil), Card(Colour.Red, Number.nine), Colour.Red, true)
    val next = UnoLogic.playCard(state, card)
    next.playerHand.count shouldBe 0
    next.isPlayerTurn shouldBe false
  }

  it should "handle player special cards (plus2, plus4)" in {
    // Test plus2
    val p2 = Card(Colour.Red, Number.plus2)
    val state2 = GameState(new Hand(List(p2)), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    UnoLogic.playCard(state2, p2).cpuHand.count shouldBe 2

    // Test plus4 with color choice
    val p4 = Card(Colour.Black, Number.plus4)
    val state4 = GameState(new Hand(List(p4)), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val next4 = UnoLogic.playCard(state4, p4, Some(Colour.Blue))
    next4.cpuHand.count shouldBe 4
    next4.activeColour shouldBe Colour.Blue
  }

  it should "handle Skip and directionChange for the player" in {
    // Player plays Skip
    val skipCard = Card(Colour.Red, Number.Skip)
    val state1 = GameState(new Hand(List(skipCard)), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val next1 = UnoLogic.playCard(state1, skipCard)
    next1.isPlayerTurn shouldBe true
    next1.statusMessage should include("Du bist nochmal dran!")

    // Player plays directionChange
    val dirCard = Card(Colour.Red, Number.directionChange)
    val state2 = GameState(new Hand(List(dirCard)), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    UnoLogic.playCard(state2, dirCard).isPlayerTurn shouldBe true
  }

  it should "return an error message for invalid moves" in {
    val wrong = Card(Colour.Blue, Number.nine)
    val state = GameState(new Hand(List(wrong)), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    UnoLogic.playCard(state, wrong).statusMessage shouldBe "Ungültiger Zug!"
  }

  // --- 3. CPU ACTIONS (cpuTurn) ---
  "UnoLogic.cpuTurn" should "correctly handle CPU special cards (plus2, plus4)" in {
    val p2 = Card(Colour.Red, Number.plus2)
    val stateP2 = GameState(new Hand(Nil), new Hand(List(p2)), Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(stateP2).playerHand.count shouldBe 2

    val p4 = Card(Colour.Black, Number.plus4)
    val stateP4 = GameState(new Hand(Nil), new Hand(List(p4)), Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(stateP4).playerHand.count shouldBe 4
  }

  it should "handle CPU Skip and directionChange correctly" in {
    // CPU plays Skip -> CPU stays active
    val skipCard = Card(Colour.Red, Number.Skip)
    val state1 = GameState(new Hand(Nil), new Hand(List(skipCard)), Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(state1).isPlayerTurn shouldBe false

    // CPU plays directionChange -> CPU stays active
    val dirCard = Card(Colour.Red, Number.directionChange)
    val state2 = GameState(new Hand(Nil), new Hand(List(dirCard)), Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(state2).isPlayerTurn shouldBe false
  }

  it should "force the CPU to draw a card if no card matches" in {
    val cpuHand = new Hand(List(Card(Colour.Blue, Number.nine)))
    val state = GameState(new Hand(Nil), cpuHand, Card(Colour.Red, Number.zero), Colour.Red, false)
    val next = UnoLogic.cpuTurn(state)
    // Covers 'case None' in cpuTurn AND 'else' in drawCard
    next.cpuHand.count shouldBe 2
    next.statusMessage shouldBe "CPU zieht."
  }

  it should "consider the CPU's color wish and handle normal turns" in {
    val wild = Card(Colour.Black, Number.choice)
    val cpuHand = new Hand(List(wild, Card(Colour.Green, Number.five)))
    val state = GameState(new Hand(Nil), cpuHand, Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(state).activeColour shouldBe Colour.Green

    // Normal CPU turn
    val normal = Card(Colour.Red, Number.one)
    val stateNormal = GameState(new Hand(Nil), new Hand(List(normal)), Card(Colour.Red, Number.zero), Colour.Red, false)
    UnoLogic.cpuTurn(stateNormal).isPlayerTurn shouldBe true
  }

  // --- 4. HELPER FUNCTIONS ---
  "UnoLogic.drawCard" should "draw a card for the player and switch turns" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    val next = UnoLogic.drawCard(state)
    next.playerHand.count shouldBe 1
    next.isPlayerTurn shouldBe false
  }
}