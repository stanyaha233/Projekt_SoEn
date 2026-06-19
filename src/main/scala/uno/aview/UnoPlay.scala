package uno.aview

import com.google.inject.Inject
import uno.controller.*
import uno.model.*
import uno.util.Observer

import scala.io.StdIn


class UnoPlay @Inject() (controller: ControllerInterface) extends Observer{

  controller.add(this)

  override def update(): Unit = {
    val state = controller.state

    if (controller.isGameActive) {
      println(s"\n--- ${state.statusMessage} ---")
      println(s"Aktuelle Farbe: ${controller.activeColour} | Karte auf Stapel: ${controller.pileCard.colour} ${controller.pileCard.value}")
      println(s"Gegner Karten: ${controller.cpuHandCount}")

      if (controller.isPlayerTurn) {
        val coloredHand = controller.playerHandCards.map(c => formatCard(c)).mkString(" ")
        println(s"Deine Hand: $coloredHand")

        if (!state.playerHand.possible(controller.pileCard)) {
          println("Du kannst nicht legen. Tippe 'draw' zum Ziehen.")
        }
      }
    } else {
      if (controller.playerHandCount == 0) println("GLÜCKWUNSCH! Du hast gewonnen!")
      else if (controller.cpuHandCount == 0) println("SCHADE! Der Gegner hat gewonnen!")
    }
  }

  private def parseInput(input: String): Unit = {
    try {
      val parts = input.split(" ")
      if (parts.length < 2) throw new IllegalArgumentException("Incomplete input")

      val col = Colour.withName(parts(0).capitalize)
      val valNum = Number.withName(parts(1))

      controller.playerHandCards.find(c => c.colour == col && c.value == valNum) match {
        case Some(c) =>
          if (c.colour == Colour.Black) {
            println("Wähle Farbe (Red, Green, Blue, Yellow):")
            val inputColor = StdIn.readLine()
            val chosen = if (inputColor != null && inputColor.trim.nonEmpty) Colour.withName(inputColor.trim.capitalize) else Colour.Red
            controller.playCard(c, Some(chosen))
          } else {
            controller.playCard(c)
          }
        case None => 
          controller.setMessage("Diese Karte hast du nicht!")
     }
    } catch {
      case _: IllegalArgumentException | _: NoSuchElementException => 
        controller.setMessage("Eingabe falsch!")
    }
  }

  def processInputLine(input: String): Unit = {
    input match {
      case "undo" => controller.undo()
      case "draw" => controller.drawCard()
      case "redo" => controller.redo()
      case _ => parseInput(input)
    }
  }
}