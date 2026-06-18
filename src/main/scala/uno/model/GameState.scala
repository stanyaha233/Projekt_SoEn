package uno.model

case class GameState(
                      playerHand: Hand,
                      cpuHand: Hand,
                      pile: Card,
                      activeColour: Colour.Value,
                      isPlayerTurn: Boolean,
                      statusMessage: String = "Spiel startet!",
                      unoSaid: Boolean = false
                    ) extends GameStateInterface

