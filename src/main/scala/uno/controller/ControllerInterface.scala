package uno.controller

import uno.model.{Card, Colour, GameStateInterface}
import uno.util.Observable

trait ControllerInterface extends Observable {
  def state: GameStateInterface
  def playerHandCards: List[Card]
  def playerHandCount: Int
  def cpuHandCount: Int
  def pileCard: Card
  def activeColour: Colour.Value
  def isPlayerTurn: Boolean
  def isGameActive: Boolean
  def playCard(card: Card, chosenColour: Option[Colour.Value] = None): Unit
  def undo(): Unit
  def cpuTurn(): Unit
  def drawCard(): Unit
  def sortHandByColor(): Unit
  def sortHandByValue(): Unit
  def setMessage(msg: String): Unit
}
