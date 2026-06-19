package uno.controller

import uno.controller.components.UnoLogic
import uno.util.Command
import uno.model.Card
import uno.model.GameStateInterface


class PlaceCardCommand(controller: UnoLogic, card: Card, chosenColour: Option[uno.model.Colour.Value]) extends Command {
  private val stateBefore: GameStateInterface = controller.state.copyState()
  private var stateAfter: Option[GameStateInterface] = None

  override def execute(): Unit = {
    if (controller.canPlay(card)) {
      controller.executePlaceCard(card, chosenColour)
      stateAfter = Some(controller.state.copyState())
    }
  }

  override def undo(): Unit = {
    controller.state = stateBefore
    controller.notifyObservers()
  }

  override def redo(): Unit = {
    stateAfter.foreach { state =>
      controller.state = state
      controller.notifyObservers()
    }
  }
}