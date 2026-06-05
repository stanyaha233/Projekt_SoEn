package uno.controller

import uno.util.Command
import uno.model.Card

class PlaceCardCommand(controller: UnoLogic, card: Card, chosenColour: Option[uno.model.Colour.Value]) extends Command {

  private val stateBefore = controller.state.copy()

  override def execute(): Unit = {
    controller.executePlaceCard(card, chosenColour)
  }

  override def undo(): Unit = {
    controller.state = stateBefore
    controller.notifyObservers()
  }
}