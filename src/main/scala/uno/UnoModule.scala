package uno

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import uno.aview.{TuiInterface, UnoPlay}
import uno.controller.{ControllerInterface, UnoLogic}
import uno.model.GameState
import uno.util.GameFactory
import com.google.inject.Provides

class UnoModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ControllerInterface].to[UnoLogic]
    bind[TuiInterface].to[UnoPlay]
  }

  @Provides
  def provideGameState(): GameState = {
    GameFactory.createInitialState()
  }
}
