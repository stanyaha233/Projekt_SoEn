package uno

// $COVERAGE-OFF$
import uno.model._
import uno.aview.*
import uno.controller.ControllerInterface
import uno.gui.SwingGui
import javax.swing.UIManager
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName)

    val injector = com.google.inject.Guice.createInjector(new UnoModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = injector.getInstance(classOf[UnoPlay])
    val gui = injector.getInstance(classOf[SwingGui])

    println("=== Willkommen zu UNO ===")

    controller.notifyObservers()

    val tuiThread = new Thread(new Runnable {
      override def run(): Unit = {
        while (controller.state.isGameActive) {
          if (controller.state.isPlayerTurn) {
            val input = StdIn.readLine("Zug (Farbe Wert) oder 'draw': ")
            if (input != null) {
              tui.processInputLine(input.trim.toLowerCase)
            }
          } else {
            Thread.sleep(1000)
            controller.cpuTurn()
          }
        }
      }
    })
    tuiThread.setDaemon(true)
    tuiThread.start()

    // Keep the main thread alive so sbt run doesn't exit immediately
    while (controller.state.isGameActive) {
      Thread.sleep(250)
    }
  }
}
// $COVERAGE-ON$