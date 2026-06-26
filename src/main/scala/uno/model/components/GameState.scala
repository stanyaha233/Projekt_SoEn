package uno.model.components

import uno.model.*

case class GameState (
                       playerHand: Hand,
                       cpuHand: Hand,
                       pile: Card,
                       activeColour: Colour.Value,
                       isPlayerTurn: Boolean,
                       statusMessage: String = "Spiel startet!",
                       unoSaid: Boolean = false
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

  override def update(pH: Hand, cH: Hand, p: Card, col: Colour.Value, turn: java.lang.Boolean, msg: String): GameState = {
    this.copy(
      playerHand = if (pH != null) pH else playerHand,
      cpuHand = if (cH != null) cH else cpuHand,
      pile = if (p != null) p else pile,
      activeColour = if (col != null) col else activeColour,
      isPlayerTurn = if (turn != null) turn.booleanValue() else isPlayerTurn,
      statusMessage = if (msg != null) msg else statusMessage
    )
  }

  override def copyState(): GameStateInterface = this.copy()
  override def updateUnoSaid(newUnoSaid: Boolean): GameState = this.copy(unoSaid = newUnoSaid)
}