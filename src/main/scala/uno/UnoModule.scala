package uno

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import uno.aview.{TuiInterface, UnoPlay}
import uno.controller.{BotStrategy, ControllerInterface, DefaultStrategyService, FirstPossibleStrategy, StrategyService, UnoLogic}
import uno.model.GameState
import uno.model.{SortByColorStrategy, SortByValueStrategy, SortingStrategy}
import uno.util.GameFactory
import com.google.inject.Provides

class UnoModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ControllerInterface].to[UnoLogic]
    bind[TuiInterface].to[UnoPlay]
    bind[StrategyService].to[DefaultStrategyService]
    bind[BotStrategy].to[FirstPossibleStrategy]
    bind[SortingStrategy].annotatedWith(Names.named("colorSorter")).to[SortByColorStrategy]
    bind[SortingStrategy].annotatedWith(Names.named("valueSorter")).to[SortByValueStrategy]
  }

  @Provides
  def provideGameState(): GameState = {
    GameFactory.createInitialState()
  }
}
