package uno.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uno.model._

class GameFactorySpec extends AnyFlatSpec with Matchers {
  "GameFactory" should "create a valid initial GameState" in {
    for (_ <- 1 to 100) {
      val state = GameFactory.createInitialState()
      state.playerHand.count should be(7)
      state.cpuHand.count should be(7)
      state.isPlayerTurn should be(true)
      
      if (state.pile.colour == Colour.Black) {
        state.activeColour should be(Colour.Red)
      } else {
        state.activeColour should be(state.pile.colour)
      }
    }
  }
}