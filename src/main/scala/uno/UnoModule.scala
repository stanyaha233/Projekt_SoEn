package uno

import com.google.inject.{AbstractModule, Provides, Singleton}
import uno.controller.components.*
import uno.controller.*
import uno.model.GameStateInterface
import uno.model.components.GameState
import uno.model.*
import uno.aview.*
import uno.util.*

class UnoModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface])
      .to(classOf[UnoLogic])
      .in(classOf[Singleton])
    bind(classOf[GameStateInterface])
      .to(classOf[GameState])
      .in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def provideFileIO(): FileIO = {
    sys.props.get("uno.fileio").map(_.trim.toLowerCase) match {
      case Some("xml")  => new FileIOXml
      case Some("json") => new FileIOJson
      case Some(other)  =>
        throw new IllegalArgumentException(
          s"Unknown file IO implementation '$other'. Use 'xml' or 'json'."
        )
      case None => new FileIOJson
    }
  }

  @Provides
  @Singleton
  def provideGameState(): GameState = {
    val playerHand = Hand(List.fill(7)(Draw.draw()))
    val cpuHand = Hand(List.fill(7)(Draw.draw()))

    val initialPile = Draw.draw()

    GameState(
      playerHand = playerHand,
      cpuHand = cpuHand,
      pile = initialPile,
      activeColour = if (initialPile.colour == Colour.Black) Colour.Red else initialPile.colour,
      isPlayerTurn = true,
      statusMessage = "Willkommen bei UNO! Das Spiel beginnt mit 7 Karten."
    )
  }
}
