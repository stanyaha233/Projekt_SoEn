package uno.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class PlaceCardCommandSpec extends AnyWordSpec with Matchers {
  "A PlaceCardCommand" should {

    // Test-Zustand: Wir wählen Farbe Blau, um zum pile zu passen
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
      // 1. Zug ausführen
      controller.playCard(cardToPlay)

      // Debug-Ausgabe zur Kontrolle
      println("Hand nach dem Zug: " + controller.state.playerHand.cards)

      // Prüfen: Da der Zug gültig ist, MUSS die Karte jetzt weg sein
      controller.state.playerHand.cards should not contain cardToPlay

      // 2. Undo ausführen
      controller.undo()

      // Debug-Ausgabe zur Kontrolle
      println("Hand nach Undo: " + controller.state.playerHand.cards)

      // 3. Prüfen, ob die Karte durch das Undo wieder da ist
      controller.state.playerHand.cards should contain(cardToPlay)

      // Bonus: Prüfen, ob der restliche Zustand auch wieder stimmt
      controller.state.pile should be(Card(Colour.Blue, Number.two))
    }

    "not execute when the card cannot be played" in {
      val invalidState = initialState.copy(activeColour = Colour.Red, pile = Card(Colour.Red, Number.two))
      val invalidController = new UnoLogic(invalidState)
      val invalidCommand = new PlaceCardCommand(invalidController, Card(Colour.Blue, Number.one), None)

      invalidCommand.execute()

      invalidController.state should be(invalidState)
    }
  }
}