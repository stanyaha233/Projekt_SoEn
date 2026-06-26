package uno.model

import uno.model.components.GameState
import uno.model.*

trait GameStateInterface {
  def playerHand: Hand
  def cpuHand: Hand
  def pile: Card
  def activeColour: Colour.Value
  def isPlayerTurn: Boolean
  def statusMessage: String
  def unoSaid: Boolean
  def isGameActive: Boolean = playerHand.count > 0 && cpuHand.count > 0
  def karteZiehen(): GameStateInterface
  def spielZugAusfuehren(card: Card): GameStateInterface
  def kartenSortieren(): GameStateInterface
  def copyState(): GameStateInterface
  def updateUnoSaid(unoSaid: Boolean): GameStateInterface

  def update(
              playerHand: Hand = null,
              cpuHand: Hand = null,
              pile: Card = null,
              activeColour: Colour.Value = null,
              isPlayerTurn: java.lang.Boolean = null,
              statusMessage: String = null
            ): GameStateInterface
}