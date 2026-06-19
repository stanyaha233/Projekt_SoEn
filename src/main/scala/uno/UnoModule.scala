package uno

import com.google.inject.{AbstractModule, Provides}
import uno.controller.components.*
import uno.controller.*
import uno.model.GameStateInterface
import uno.model.components.GameState
import uno.model.*
import uno.aview.*

class UnoModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[UnoLogic])
    bind(classOf[GameStateInterface]).to(classOf[GameState])
  }

  @Provides
  def provideGameState(): GameState = {
    val playerHand = Hand(List.fill(7)(Draw.draw()))
    val cpuHand = Hand(List.fill(7)(Draw.draw()))

    val initialPile = Draw.draw()

    GameState(
      playerHand = playerHand,
      cpuHand = cpuHand,
      pile = initialPile,
      activeColour = initialPile.colour,
      isPlayerTurn = true,
      statusMessage = "Willkommen bei UNO! Das Spiel beginnt mit 7 Karten."
    )
  }
}