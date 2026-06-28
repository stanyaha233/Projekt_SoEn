package uno.model.components

import uno.model.*

case class GameState (
                       playerHand: Hand,
                       cpuHand: Hand,
                       pile: Card,
                       activeColour: Colour.Value,
                       isPlayerTurn: Boolean,
                       statusMessage: String = "Spiel startet!",
                       unoSaid: Boolean = false,
                       playerTotalScore: Int = 0, 
                       cpuTotalScore: Int = 0
                     ) extends GameStateInterface {

  override def karteZiehen(): GameState = {
    val gezogeneKarte = Draw.draw()
    this.copy(playerHand = playerHand.add(gezogeneKarte))
  }


  override def spielZugAusfuehren(card: Card): GameState = {
    this.copy(
      playerHand = new Hand(playerHand.cards.filterNot(_ == card)),
      pile = card,
      activeColour = card.colour
    )
  }

  override def kartenSortieren(): GameState = {
    val standardStrategie = new SortByColorStrategy()

    val unsortierteListe = playerHand.cards
    val sortierteListe = standardStrategie.sort(unsortierteListe)

    this.copy(playerHand = Hand(sortierteListe))
  }

  override def update(
                       pH: Hand = null,
                       cH: Hand = null,
                       p: Card = null,
                       col: Colour.Value = null,
                       turn: java.lang.Boolean = null,
                       msg: String = null,
                       pScore: Int = -1, // -1 als Indikator, dass kein neuer Score gesetzt wurde
                       cScore: Int = -1
                     ): GameState = {
    this.copy(
      playerHand = if (pH != null) pH else playerHand,
      cpuHand = if (cH != null) cH else cpuHand,
      pile = if (p != null) p else pile,
      activeColour = if (col != null) col else activeColour,
      isPlayerTurn = if (turn != null) turn.booleanValue() else isPlayerTurn,
      statusMessage = if (msg != null) msg else statusMessage,
      playerTotalScore = if (pScore != -1) pScore else playerTotalScore,
      cpuTotalScore = if (cScore != -1) cScore else cpuTotalScore
    )
  }

  override def copyState(): GameStateInterface = this.copy()
  override def updateUnoSaid(newUnoSaid: Boolean): GameState = this.copy(unoSaid = newUnoSaid)
}