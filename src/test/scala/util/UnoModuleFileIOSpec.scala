package uno.util

import org.scalatest.funsuite.AnyFunSuite
import uno.UnoModule
import uno.controller.ControllerInterface
import uno.model.GameStateInterface
import com.google.inject.Guice

class UnoModuleFileIOSpec extends AnyFunSuite {
  private val module = new UnoModule

  test("provideFileIO should default to JSON") {
    System.clearProperty("uno.fileio")

    val fileIO = module.provideFileIO()

    assert(fileIO.isInstanceOf[FileIOJson])
  }

  test("provideFileIO should switch to XML via system property") {
    System.setProperty("uno.fileio", "xml")

    try {
      val fileIO = module.provideFileIO()
      assert(fileIO.isInstanceOf[FileIOXml])
    } finally {
      System.clearProperty("uno.fileio")
    }
  }

  test("provideFileIO should switch to JSON via system property") {
    System.setProperty("uno.fileio", "json")

    try {
      val fileIO = module.provideFileIO()
      assert(fileIO.isInstanceOf[FileIOJson])
    } finally {
      System.clearProperty("uno.fileio")
    }
  }

  test("provideFileIO should throw IllegalArgumentException for unknown type") {
    System.setProperty("uno.fileio", "unknown")
    try {
      intercept[IllegalArgumentException] {
        module.provideFileIO()
      }
    } finally {
      System.clearProperty("uno.fileio")
    }
  }

  test("provideGameState should initialize correctly") {
    val state = module.provideGameState()
    assert(state.playerHand.count == 7)
    assert(state.cpuHand.count == 7)
    assert(state.isPlayerTurn)
  }

  test("configure should bind dependencies correctly") {
    val injector = Guice.createInjector(module)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val gameState = injector.getInstance(classOf[GameStateInterface])
    assert(controller != null)
    assert(gameState != null)
  }
}
