package uno.controller

import uno.model.{Card, Colour, Draw, Hand}
import uno.controller.components.UnoLogic
import scala.util.{Success, Failure, Try}
//State Pattern
trait TurnState {
  def playCard(
      controller: UnoLogic,
      card: Card,
      chosenColour: Option[Colour.Value] = None
  ): Unit
  def drawCard(controller: UnoLogic): Unit
  def cpuTurn(controller: UnoLogic): Unit
}

object PlayerTurnState extends TurnState {
  override def playCard(
      controller: UnoLogic,
      card: Card,
      chosenColour: Option[Colour.Value] = None
  ): Unit = {
    if (controller.canPlay(card)) {
      controller.executePlaceCardCommand(card, chosenColour)
      if (!controller.state.isPlayerTurn) {
        controller.turnState = CpuTurnState
        controller.notifyObservers()
        controller.cpuTurn()
      } else {
        controller.notifyObservers()
      }
    } else {
      controller.state =
        controller.state.update(statusMessage = "Ungültiger Zug!")
      controller.notifyObservers()
    }
  }

  override def drawCard(controller: UnoLogic): Unit = {
    val drawResult = Try(Draw.draw())
    drawResult match {
      case Success(newCard) =>
        controller.state = controller.state.update(
          playerHand =
            controller.autoSort(controller.state.playerHand.add(newCard)),
          isPlayerTurn = false,
          statusMessage = "Karte gezogen. Gegner ist am Zug."
        )
        controller.turnState = CpuTurnState
        controller.notifyObservers()
        controller.cpuTurn()
      case Failure(_) =>
        controller.state =
          controller.state.update(statusMessage = "Stapel ist leer!")
        controller.notifyObservers()
    }
  }

  override def cpuTurn(controller: UnoLogic): Unit = {}
}

object CpuTurnState extends TurnState {
  override def playCard(
      controller: UnoLogic,
      card: Card,
      chosenColour: Option[Colour.Value] = None
  ): Unit = {}

  override def drawCard(controller: UnoLogic): Unit = {
    val drawResult = Try(Draw.draw())
    drawResult match {
      case Success(newCard) =>
        controller.state = controller.state.update(
          cpuHand = controller.state.cpuHand.add(newCard),
          isPlayerTurn = true,
          statusMessage = "CPU hat gezogen."
        )
        controller.turnState = PlayerTurnState
        controller.notifyObservers()
      case Failure(_) =>
        controller.state =
          controller.state.update(statusMessage = "Stapel ist leer!")
        controller.notifyObservers()
    }
  }

  override def cpuTurn(controller: UnoLogic): Unit = {
    controller.state.cpuHand.cards.find(c => controller.canPlay(c)) match {
      case Some(card) =>
        val newCpuHand = new Hand(
          controller.state.cpuHand.cards.filterNot(_ == card)
        )
        var newPlayerHand = controller.state.playerHand
        var nextTurnIsPlayer = true
        val cpuWish = newCpuHand.cards.headOption
          .map(_.colour)
          .find(_ != Colour.Black)
          .getOrElse(Colour.Red)
        val nextColour =
          if (card.colour == Colour.Black) cpuWish else card.colour

        card.value match {
          case uno.model.Number.plus2 =>
            for (_ <- 1 to 2) newPlayerHand = newPlayerHand.add(Draw.draw())
          case uno.model.Number.plus4 =>
            for (_ <- 1 to 4) newPlayerHand = newPlayerHand.add(Draw.draw())
          case uno.model.Number.skip | uno.model.Number.directionchange =>
            nextTurnIsPlayer = false
          case _ => nextTurnIsPlayer = true
        }

        val cpuUnoMsg = if (newCpuHand.count == 1) " und sagt UNO!" else ""
        controller.state = controller.state.update(
          cpuHand = newCpuHand,
          playerHand = controller.autoSort(newPlayerHand),
          pile = card,
          activeColour = nextColour,
          isPlayerTurn = nextTurnIsPlayer,
          statusMessage =
            s"Gegner legt ${card.colour} ${card.value}" + cpuUnoMsg
        )

        if (nextTurnIsPlayer) {
          controller.turnState = PlayerTurnState
          controller.notifyObservers()
        } else {
          controller.notifyObservers()
          if (controller.isGameActive) {
            controller.cpuTurn()
          }
        }
      case None =>
        drawCard(controller)
    }
  }
}
