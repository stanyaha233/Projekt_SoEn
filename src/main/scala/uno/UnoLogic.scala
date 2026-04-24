package uno

case class GameState(
                      playerHand: Hand,
                      cpuHand: Hand,
                      pile: Card,
                      activeColour: Colour.Value,
                      isPlayerTurn: Boolean,
                      statusMessage: String = "Spiel startet!",
                      unoSaid: Boolean = false
                    )


object UnoLogic {
  def canPlay(card: Card, state: GameState): Boolean = {
    card.colour == state.activeColour ||
    card.value == state.pile.value ||
    card.colour == Colour.Black
  }


  def playCard(state: GameState, card: Card, chosenColour: Option[Colour.Value] = None): GameState = {
    if (!canPlay(card, state)) return state.copy(statusMessage = "Ungültiger Zug!")
    val newPlayerHand = new Hand(state.playerHand.cards.diff(List(card)))
    var newCpuHand = state.cpuHand
    var nextTurnIsPlayer = false
    val nextColour = if (card.colour == Colour.Black) chosenColour.getOrElse(Colour.Red) else card.colour
    var msg = s"Du legst ${card.colour} ${card.value}"

    card.value match {
      case Number.plus2 =>
        for (_ <- 1 to 2) newCpuHand = newCpuHand.add(Draw.draw())
        msg += ". CPU zieht 2!"
      case Number.plus4 =>
        for (_ <- 1 to 4) newCpuHand = newCpuHand.add(Draw.draw())
        msg += ". CPU zieht 4!"
      case Number.skip | Number.directionchange =>
        nextTurnIsPlayer = true
        msg += ". Du bist nochmal dran!"
      case _ =>
        nextTurnIsPlayer = false
    }


    state.copy(
      playerHand = newPlayerHand,
      cpuHand = newCpuHand,
      pile = card,
      activeColour = nextColour,
      isPlayerTurn = nextTurnIsPlayer,
      statusMessage = msg
    )
  }


  def cpuTurn(state: GameState): GameState = {
    state.cpuHand.cards.find(c => canPlay(c, state)) match {
      case Some(card) =>
        val newCpuHand = new Hand(state.cpuHand.cards.filterNot(_ == card))
        var newPlayerHand = state.playerHand
        var nextTurnIsPlayer = true
        val cpuWish = state.cpuHand.cards.headOption.map(_.colour).find(_ != Colour.Black).getOrElse(Colour.Red)
        val nextColour = if (card.colour == Colour.Black) cpuWish else card.colour
        card.value match {
          case Number.plus2 => for (_ <- 1 to 2) newPlayerHand = newPlayerHand.add(Draw.draw())
          case Number.plus4 => for (_ <- 1 to 4) newPlayerHand = newPlayerHand.add(Draw.draw())
          case Number.skip | Number.directionchange => nextTurnIsPlayer = false
          case _ => nextTurnIsPlayer = true
        }

        state.copy(
          cpuHand = newCpuHand,
          playerHand = newPlayerHand,
          pile = card,
          activeColour = nextColour,
          isPlayerTurn = nextTurnIsPlayer,
          statusMessage = s"Gegner legt ${card.colour} ${card.value}"
        )
      case None => drawCard(state)
    }
  }


  def drawCard(state: GameState): GameState = {
    val newCard = Draw.draw()
    if (state.isPlayerTurn) {
      state.copy(playerHand = state.playerHand.add(newCard), isPlayerTurn = false, statusMessage = "Gezogen.")
    } else {
      state.copy(cpuHand = state.cpuHand.add(newCard), isPlayerTurn = true, statusMessage = "CPU zieht.")
    }
  }
} 