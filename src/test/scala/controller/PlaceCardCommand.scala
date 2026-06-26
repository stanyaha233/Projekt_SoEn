package uno.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import uno.controller.components.UnoLogic
import uno.model.*
import uno.model.components.GameState

class PlaceCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlaceCardCommand" should {

    val initialState = GameState(
      playerHand = Hand(List(Card(Colour.Blue, Number.one))),
      cpuHand = Hand(List()),
      pile = Card(Colour.Blue, Number.two),
      activeColour = Colour.Blue,
      isPlayerTurn = true,
      statusMessage = ""
    )

    val controller = new UnoLogic(initialState)
    val cardToPlay = Card(Colour.Blue, Number.one)

    "correctly undo a played card" in {
      controller.playCard(cardToPlay)

      println("Hand nach dem Zug: " + controller.state.playerHand.cards)

      controller.state.playerHand.cards should not contain cardToPlay

      controller.undo()

      println("Hand nach Undo: " + controller.state.playerHand.cards)

      controller.state.playerHand.cards should contain(cardToPlay)

      controller.state.pile should be(Card(Colour.Blue, Number.two))

      controller.redo()
      controller.state.playerHand.cards should not contain cardToPlay
      controller.state.pile should be(cardToPlay)
    }

    "not execute when the card cannot be played" in {
      val invalidState = initialState.copy(
        activeColour = Colour.Red,
        pile = Card(Colour.Red, Number.two)
      )
      val invalidController = new UnoLogic(invalidState)
      val invalidCommand = new PlaceCardCommand(
        invalidController,
        Card(Colour.Blue, Number.one),
        None
      )

      invalidCommand.execute()

      invalidController.state should be(invalidState)
    }
  }
}
