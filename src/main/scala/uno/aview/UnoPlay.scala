package uno.aview

import scala.io.StdIn
import uno.model._
import uno.controller.ControllerInterface
import uno.util.Observer

trait TuiInterface {
  def processInputLine(input: String): Unit
}

class UnoPlay(controller: ControllerInterface) extends Observer with TuiInterface {

  controller.add(this)

  override def update(): Unit = {
    val state = controller.state

    if (state.playerHand.count > 0 && state.cpuHand.count > 0) {
      println(s"\n--- ${state.statusMessage} ---")
      println(s"Aktuelle Farbe: ${state.activeColour} | Karte auf Stapel: ${state.pile.colour} ${state.pile.value}")
      println(s"Gegner Karten: ${state.cpuHand.count}")

      if (state.isPlayerTurn) {
        val coloredHand = state.playerHand.cards.map(c => formatCard(c)).mkString(" ")
        println(s"Deine Hand: $coloredHand")

        if (!state.playerHand.possible(state.pile)) {
          println("Du kannst nicht legen. Tippe 'draw' zum Ziehen.")
        }
      }
    } else {
      if (state.playerHand.count == 0) println("GLÜCKWUNSCH! Du hast gewonnen!")
      else if (state.cpuHand.count == 0) println("SCHADE! Der Gegner hat gewonnen!")
    }
  }

  private def parseInput(input: String): Unit = {
    try {
      val parts = input.split(" ")
      if (parts.length < 2) throw new IllegalArgumentException("Incomplete input")

      val col = Colour.withName(parts(0).capitalize)
      val valNum = Number.withName(parts(1))

      controller.state.playerHand.cards.find(c => c.colour == col && c.value == valNum) match {
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

  override def processInputLine(input: String): Unit = {
    input match {
      case "undo" => controller.undo()
      case "draw" => controller.drawCard()
      case _ => parseInput(input)
    }
  }
}