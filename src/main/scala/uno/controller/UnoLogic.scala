package uno.controller

import uno.model._
import uno.util.Observable
import com.google.inject.Inject
import uno.util.UndoManager
import uno.util.Command
import scala.util.{Try, Success, Failure}


class UnoLogic @Inject() (var state: GameState) extends ControllerInterface {
  private val undoManager = new UndoManager()

  override def playerHandCards: List[Card] = state.playerHand.cards
  override def playerHandCount: Int = state.playerHand.count
  override def cpuHandCount: Int = state.cpuHand.count
  override def pileCard: Card = state.pile
  override def activeColour: Colour.Value = state.activeColour
  override def isPlayerTurn: Boolean = state.isPlayerTurn
  override def isGameActive: Boolean = state.isGameActive

  private def autoSort(hand: Hand): Hand = {
    new Hand(new SortByColorStrategy().sort(hand.cards))
  }

  def canPlay(card: Card): Boolean = {
    card.colour == state.activeColour ||
      card.value == state.pile.value ||
      card.colour == Colour.Black
  }

  def executePlaceCard(card: Card, chosenColour: Option[Colour.Value] = None): Unit = {
    if (!canPlay(card)) {
      state = state.copy(statusMessage = "Ungültiger Zug!")
    } else {
      val newPlayerHand = new Hand(state.playerHand.cards.diff(List(card)))
      val nextColour = if (card.colour == Colour.Black) chosenColour.getOrElse(Colour.Red) else card.colour

      val (newCpuHand, nextTurnIsPlayer, msgSuffix) = card.value match {
        case Number.plus2 => (state.cpuHand.add(Draw.draw()).add(Draw.draw()), false, ". CPU zieht 2!")
        case Number.plus4 => (state.cpuHand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw()), false, ". CPU zieht 4!")
        case Number.skip | Number.directionchange => (state.cpuHand, true, ". Du bist nochmal dran!")
        case _ => (state.cpuHand, false, "")
      }

      state = state.copy(
        playerHand = autoSort(newPlayerHand),
        cpuHand = newCpuHand,
        pile = card,
        activeColour = nextColour,
        isPlayerTurn = nextTurnIsPlayer,
        statusMessage = s"Du legst ${card.colour} ${card.value}" + msgSuffix
      )
    }
  }


  def playCard(card: Card, chosenColour: Option[Colour.Value] = None): Unit = {
    if (canPlay(card)) {
      undoManager.executeCommand(new PlaceCardCommand(this, card, chosenColour))

      if (!state.isPlayerTurn) {
        cpuTurn()
      } else {
        notifyObservers()
      }
    } else {
      state = state.copy(statusMessage = "Ungültiger Zug!")
      notifyObservers()
    }
  }

  def undo(): Unit = {
    undoManager.undo()
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
          playerHand = autoSort(newPlayerHand),
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
    val drawResult: Try[Card] = Try(Draw.draw())

    drawResult match {
      case Success(newCard) =>
        if (state.isPlayerTurn) {
          state = state.copy(
            playerHand = autoSort(state.playerHand.add(newCard)),
            isPlayerTurn = false,
            statusMessage = "Karte gezogen. Gegner ist am Zug."
          )
          notifyObservers()
          cpuTurn()
        } else {
          state = state.copy(
            cpuHand = state.cpuHand.add(newCard),
            isPlayerTurn = true,
            statusMessage = "CPU hat gezogen."
          )
          notifyObservers()
        }
      case Failure(_) =>
        state = state.copy(statusMessage = "Stapel ist leer!")
        notifyObservers()
    }
  }

  def sortHandByColor(): Unit = {
        val sortedCards = new SortByColorStrategy().sort(state.playerHand.cards)
        val sortedHand = new Hand(sortedCards)
        state = state.copy(playerHand = sortedHand, statusMessage = "Karten wurden nach Farbe sortiert.")
        notifyObservers()
  }

  def sortHandByValue(): Unit = {
        val sortedCards = new SortByValueStrategy().sort(state.playerHand.cards)
        val sortedHand = new Hand(sortedCards)
        state = state.copy(playerHand = sortedHand, statusMessage = "Karten wurden nach Zahl sortiert.")
        notifyObservers()
      }

  def setMessage(msg: String): Unit = {
    state = state.copy(statusMessage = msg)
    notifyObservers()
  }
}
  