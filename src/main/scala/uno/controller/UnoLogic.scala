package uno.controller

import uno.model._
import uno.util.Observable

class UnoLogic(var state: GameState) extends Observable {
  def canPlay(card: Card): Boolean = {
    card.colour == state.activeColour ||
    card.value == state.pile.value ||
    card.colour == Colour.Black
  }

  def playCard(card: Card, chosenColour: Option[Colour.Value] = None): Unit = {
    if (!canPlay(card)) {
      state = state.copy(statusMessage = "Ungültiger Zug!")
    } else {
      val newPlayerHand = new Hand(state.playerHand.cards.diff(List(card)))
      val nextColour = if (card.colour == Colour.Black) chosenColour.getOrElse(Colour.Red) else card.colour
      
      val (newCpuHand, nextTurnIsPlayer, msgSuffix) = card.value match {
        case Number.plus2 =>
          (state.cpuHand.add(Draw.draw()).add(Draw.draw()), false, ". CPU zieht 2!")
        case Number.plus4 =>
          (state.cpuHand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw()), false, ". CPU zieht 4!")
        case Number.skip | Number.directionchange =>
          (state.cpuHand, true, ". Du bist nochmal dran!")
        case _ =>
          (state.cpuHand, false, "")
      }
      
      val msg = s"Du legst ${card.colour} ${card.value}$msgSuffix"
  
      state = state.copy(
        playerHand = newPlayerHand,
        cpuHand = newCpuHand,
        pile = card,
        activeColour = nextColour,
        isPlayerTurn = nextTurnIsPlayer,
        statusMessage = msg
      )
    }
    notifyObservers()
  }

  def cpuTurn(): Unit = {
    state.cpuHand.cards.find(c => canPlay(c)) match {
      case Some(card) =>
        val newCpuHand = new Hand(state.cpuHand.cards.filterNot(_ == card))
        var newPlayerHand = state.playerHand
        var nextTurnIsPlayer = true
        val cpuWish = newCpuHand.cards.headOption.map(_.colour).find(_ != Colour.Black).getOrElse(Colour.Red)
        val nextColour = if (card.colour == Colour.Black) cpuWish else card.colour
        card.value match {
          case Number.plus2 => for (_ <- 1 to 2) newPlayerHand = newPlayerHand.add(Draw.draw())
          case Number.plus4 => for (_ <- 1 to 4) newPlayerHand = newPlayerHand.add(Draw.draw())
          case Number.skip | Number.directionchange => nextTurnIsPlayer = false
          case _ => nextTurnIsPlayer = true
        }

        state = state.copy(
          cpuHand = newCpuHand,
          playerHand = newPlayerHand,
          pile = card,
          activeColour = nextColour,
          isPlayerTurn = nextTurnIsPlayer,
          statusMessage = s"Gegner legt ${card.colour} ${card.value}"
        )
        notifyObservers()
      case None => drawCard()
    }
  }

  def drawCard(): Unit = {
    val newCard = Draw.draw()
    if (state.isPlayerTurn) {
      state = state.copy(playerHand = state.playerHand.add(newCard), isPlayerTurn = false, statusMessage = "Gezogen.")
    } else {
      state = state.copy(cpuHand = state.cpuHand.add(newCard), isPlayerTurn = true, statusMessage = "CPU zieht.")
    }
    notifyObservers()
  }
} 