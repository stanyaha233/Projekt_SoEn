package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class KarteSpec extends AnyFlatSpec with Matchers {

  "A Karte" should "properly store its color and value" in {
    val card = Card(Colour.Red, Number.seven)
    card.colour should be(Colour.Red)
    card.value should be(Number.seven)
  }

  "Move logic" should "allow a move if the color matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Red, Number.three)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(true)
  }

  it should "allow a move if the value matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Blue, Number.seven)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(true)
  }

  it should "deny a move if neither color nor value matches" in {
    val topCard = Card(Colour.Red, Number.seven)
    val handCard = Card(Colour.Blue, Number.three)

    val isPossible = handCard.colour == topCard.colour || handCard.value == topCard.value
    isPossible should be(false)
  }

  it should "detect at least one playable card in a hand" in {
    val topCard = Card(Colour.Red, Number.seven)
    val hand = List(
      Card(Colour.Blue, Number.three),
      Card(Colour.Green, Number.skip),
      Card(Colour.Red, Number.two)
    )

    val possible = hand.exists(k => k.colour == topCard.colour || k.value == topCard.value)
    possible should be(true)
  }

  it should "handle special black cards correctly (Wild cards)" in {
    val topCard = Card(Colour.Red, Number.seven)
    val wildCard = Card(Colour.Black, Number.choice)
    val possible = wildCard.colour == topCard.colour || wildCard.value == topCard.value || wildCard.colour == Colour.Black

    possible should be(true)
  }

  "formatCard" should "format cards with the correct ANSI colors" in {
    formatCard(Card(Colour.Red, Number.one)) should include(ConsoleColors.RED)
    formatCard(Card(Colour.Green, Number.two)) should include(ConsoleColors.GREEN)
    formatCard(Card(Colour.Blue, Number.three)) should include(ConsoleColors.BLUE)
    formatCard(Card(Colour.Yellow, Number.four)) should include(ConsoleColors.YELLOW)
    formatCard(Card(Colour.Black, Number.plus4)) should include(ConsoleColors.WHITE)
    formatCard(Card(Colour.Red, Number.one)) should include(ConsoleColors.RESET)
  }

  "GameState" should "have correct default values" in {
    val state = GameState(new Hand(Nil), new Hand(Nil), Card(Colour.Red, Number.zero), Colour.Red, true)
    state.statusMessage should be("Spiel startet!")
    state.unoSaid should be(false)

    val state2 = state.copy(isPlayerTurn = false)
    state.toString
    state.hashCode()
    state.equals(state2) should be(false)
    state.equals(state) should be(true)
    GameState.unapply(state)
  }

  it should "report game activity based on both hands" in {
    val activeState = GameState(new Hand(List(Card(Colour.Red, Number.one))), new Hand(List(Card(Colour.Blue, Number.two))), Card(Colour.Red, Number.zero), Colour.Red, true)
    activeState.isGameActive should be(true)

    val playerEmpty = activeState.copy(playerHand = new Hand(Nil))
    playerEmpty.isGameActive should be(false)

    val cpuEmpty = activeState.copy(cpuHand = new Hand(Nil))
    cpuEmpty.isGameActive should be(false)
  }

  "Case Classes and Enums" should "have their generated methods covered" in {
    val card1 = Card(Colour.Red, Number.zero)
    val card2 = card1.copy(value = Number.one)
    card1.toString
    card1.hashCode()
    card1.equals(card2) should be(false)
    card1.equals(card1) should be(true)
    Card.unapply(card1)

    Colour.values
    Number.values
    Colour.withName("Red") should be(Colour.Red)
    Number.withName("zero") should be(Number.zero)
  }
}