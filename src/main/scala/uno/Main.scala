package uno

// $COVERAGE-OFF$
import uno.model._
import uno.aview.*
import uno.controller.ControllerInterface
import uno.gui.SwingGui
import javax.swing.UIManager
import scala.io.StdIn
import com.google.inject.Guice

object Main {
  def main(args: Array[String]): Unit = {

    val gui: Option[SwingGui] = try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName)
      val injector = Guice.createInjector(new UnoModule)
      Some(injector.getInstance(classOf[SwingGui]))
    } catch {
      case e: Exception =>
        println("Info: GUI konnte nicht gestartet werden (Headless-Modus).")
        None
    }

    val injector = Guice.createInjector(new UnoModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = injector.getInstance(classOf[UnoPlay])

    println("=== Willkommen zu UNO ===")

    controller.notifyObservers()

    val tuiThread = new Thread(new Runnable {
      override def run(): Unit = {
        while (gui.exists(_.visible) || controller.state.isGameActive) {
          if (controller.state.isGameActive) {
            if (controller.state.isPlayerTurn) {
              val input = StdIn.readLine("Zug (Farbe Wert) oder 'draw': ")
              if (input != null) {
                tui.processInputLine(input.trim.toLowerCase)
              }
            } else {
              Thread.sleep(1000)
              controller.cpuTurn()
            }
          } else {
            Thread.sleep(250)
          }
        }
      }
    })
    tuiThread.setDaemon(true)
    tuiThread.start()

    while (gui.exists(_.visible) || controller.state.isGameActive) {
      Thread.sleep(250)
    }
  }
}
// $COVERAGE-ON$