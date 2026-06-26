package uno.controller.components

import com.google.inject.Inject
import uno.controller.*
import uno.model.{
  Card,
  Colour,
  Draw,
  GameStateInterface,
  Hand,
  Number,
  SortByColorStrategy,
  SortByValueStrategy
}
import uno.util.{Observable, UndoManager, FileIO, FileIOJson}
import uno.model.components.GameState
import uno.model.ScoreVisitor

import scala.util.{Failure, Success, Try}

class UnoLogic @Inject() (var state: GameStateInterface, val fileIO: FileIO)
    extends ControllerInterface {
  def this(state: GameStateInterface) = this(state, new FileIOJson)
  private val undoManager = new UndoManager()
  var turnState: TurnState =
    if (state.isPlayerTurn) PlayerTurnState else CpuTurnState

  def syncTurnState(): Unit = {
    turnState = if (state.isPlayerTurn) PlayerTurnState else CpuTurnState
  }

  def executePlaceCardCommand(
      card: Card,
      chosenColour: Option[Colour.Value]
  ): Unit = {
    undoManager.executeCommand(new PlaceCardCommand(this, card, chosenColour))
  }

  override def playerHandCards: List[Card] = state.playerHand.cards
  override def playerHandCount: Int = state.playerHand.count
  override def cpuHandCount: Int = state.cpuHand.count
  override def pileCard: Card = state.pile
  override def activeColour: Colour.Value = state.activeColour
  override def isPlayerTurn: Boolean = state.isPlayerTurn
  override def isGameActive: Boolean = state.isGameActive

  def autoSort(hand: Hand): Hand = {
    new Hand(new SortByColorStrategy().sort(hand.cards))
  }

  def canPlay(card: Card): Boolean = {
    card.colour == state.activeColour ||
    card.value == state.pile.value ||
    card.colour == Colour.Black
  }

  def executePlaceCard(
      card: Card,
      chosenColour: Option[Colour.Value] = None
  ): Unit = {
    if (!canPlay(card)) {
      state = state.update(statusMessage = "Ungültiger Zug!")
    } else {
      val newPlayerHand = new Hand(state.playerHand.cards.diff(List(card)))
      val nextColour =
        if (card.colour == Colour.Black) chosenColour.getOrElse(Colour.Red)
        else card.colour

      val (newCpuHand, nextTurnIsPlayer, msgSuffix) = card.value match {
        case Number.plus2 =>
          (
            state.cpuHand.add(Draw.draw()).add(Draw.draw()),
            false,
            ". CPU zieht 2!"
          )
        case Number.plus4 =>
          (
            state.cpuHand
              .add(Draw.draw())
              .add(Draw.draw())
              .add(Draw.draw())
              .add(Draw.draw()),
            false,
            ". CPU zieht 4!"
          )
        case Number.skip | Number.directionchange =>
          (state.cpuHand, true, ". Du bist nochmal dran!")
        case _ => (state.cpuHand, false, "")
      }

      state = state.update(
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
    turnState.playCard(this, card, chosenColour)
  }

  def undo(): Unit = {
    undoManager.undo()
    syncTurnState()
    notifyObservers()
  }

  def redo(): Unit = {
    undoManager.redo()
    syncTurnState()
    notifyObservers()
  }

  def cpuTurn(): Unit = {
    turnState.cpuTurn(this)
  }

  def drawCard(): Unit = {
    turnState.drawCard(this)
  }

  def sortHandByColor(): Unit = {
    val sortedCards = new SortByColorStrategy().sort(state.playerHand.cards)
    state = state.update(
      playerHand = new Hand(sortedCards),
      statusMessage = "Karten wurden nach Farbe sortiert."
    )
    notifyObservers()
  }

  def sortHandByValue(): Unit = {
    val sortedCards = new SortByValueStrategy().sort(state.playerHand.cards)
    state = state.update(
      playerHand = new Hand(sortedCards),
      statusMessage = "Karten wurden nach Zahl sortiert."
    )
    notifyObservers()
  }

  def setMessage(msg: String): Unit = {
    state = state.update(statusMessage = msg)
    notifyObservers()
  }

  override def save(): Unit = {
    val extension =
      sys.props.get("uno.fileio").map(_.trim.toLowerCase).getOrElse("json")
    val filename = s"savegame.$extension"

    val data = Map(
      "activeColour" -> state.activeColour.toString,
      "isPlayerTurn" -> state.isPlayerTurn.toString,
      "statusMessage" -> state.statusMessage,
      "unoSaid" -> state.unoSaid.toString,
      "pile" -> s"${state.pile.colour}:${state.pile.value}",
      "playerHand" -> state.playerHand.cards
        .map(c => s"${c.colour}:${c.value}")
        .mkString(","),
      "cpuHand" -> state.cpuHand.cards
        .map(c => s"${c.colour}:${c.value}")
        .mkString(",")
    )
    fileIO.save(data, filename)
    state =
      state.update(statusMessage = s"Spielstand gespeichert in $filename!")
    notifyObservers()
  }

  override def load(): Unit = {
    val extension =
      sys.props.get("uno.fileio").map(_.trim.toLowerCase).getOrElse("json")
    val filename = s"savegame.$extension"

    try {
      val data = fileIO.load(filename)

      def parseCard(s: String): Card = {
        val parts = s.split(":")
        Card(Colour.withName(parts(0)), Number.withName(parts(1)))
      }

      val playerHandCards = data.get("playerHand") match {
        case Some(str) if str.trim.nonEmpty =>
          str.split(",").map(parseCard).toList
        case _ => Nil
      }
      val cpuHandCards = data.get("cpuHand") match {
        case Some(str) if str.trim.nonEmpty =>
          str.split(",").map(parseCard).toList
        case _ => Nil
      }

      val loadedPile = parseCard(data("pile"))
      val loadedActiveColour = Colour.withName(data("activeColour"))
      val loadedIsPlayerTurn = data("isPlayerTurn").toBoolean
      val loadedStatusMessage =
        data.getOrElse("statusMessage", "Spiel geladen!")
      val loadedUnoSaid = data.getOrElse("unoSaid", "false").toBoolean

      state = GameState(
        playerHand = Hand(playerHandCards),
        cpuHand = Hand(cpuHandCards),
        pile = loadedPile,
        activeColour = loadedActiveColour,
        isPlayerTurn = loadedIsPlayerTurn,
        statusMessage = loadedStatusMessage,
        unoSaid = loadedUnoSaid
      )
      syncTurnState()
      notifyObservers()
    } catch {
      case e: Exception =>
        state = state.update(statusMessage =
          s"Fehler beim Laden von $filename: ${e.getMessage}"
        )
        notifyObservers()
    }
  }

  def calculateHandScore(hand: Hand): Int = {
    val visitor = new ScoreVisitor()
    hand.cards.foreach(_.accept(visitor))
    visitor.score
  }

  override def cpuHandCards: List[Card] = state.cpuHand.cards

  override def restart(): Unit = {
    val initialState: GameStateInterface =
      uno.util.GameFactory.createInitialState()
    state = initialState.update(
      statusMessage = "Spiel neu gestartet!",
      isPlayerTurn = initialState.isPlayerTurn
    )
    syncTurnState()
    notifyObservers()
  }
}
