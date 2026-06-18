package uno.model

trait GameStateInterface {
  def playerHand: Hand
  def cpuHand: Hand
  def pile: Card
  def activeColour: Colour.Value
  def isPlayerTurn: Boolean
  def statusMessage: String
  def unoSaid: Boolean

  def isGameActive: Boolean = playerHand.count > 0 && cpuHand.count > 0
}
