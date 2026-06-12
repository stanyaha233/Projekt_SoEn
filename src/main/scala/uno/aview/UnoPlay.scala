package uno.aview

import scala.io.StdIn
import uno.model._
import uno.controller.UnoLogic
import uno.util.Observer

class UnoPlay(controller: UnoLogic) extends Observer {

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

  def parseInput(input: String): Unit = {
    try {
      val parts = input.split(" ")
      if (parts.length < 2) throw new IllegalArgumentException("Incomplete input")

      val col = Colour.withName(parts(0).capitalize)
      val valNum = Number.withName(parts(1))

      controller.state.playerHand.cards.find(c => c.colour == col && c.value == valNum) match {
        case Some(c) =>
          if (c.colour == Colour.Black) {
            println("Wähle Farbe (Red, Green, Blue, Yellow):")
            val chosen = Colour.withName(StdIn.readLine().capitalize)
            controller.playCard(c, Some(chosen))
          } else {
            controller.playCard(c)
          }
        case None => 
          controller.state = controller.state.copy(statusMessage = "Diese Karte hast du nicht!")
          controller.notifyObservers()
      }
    } catch {
      case _: IllegalArgumentException | _: NoSuchElementException => 
        controller.state = controller.state.copy(statusMessage = "Eingabe falsch!")
        controller.notifyObservers()
    }
  }

  def readInput(): Unit = {
    while (controller.state.playerHand.count > 0 && controller.state.cpuHand.count > 0) {
      if (controller.state.isPlayerTurn) {
        val input = StdIn.readLine("Zug (Farbe Wert) oder 'draw': ").trim.toLowerCase

        input match {
          case "undo" =>
            controller.undo()
          case "draw" =>
            controller.drawCard()
          case _ =>
            parseInput(input)
        }
      } else {
        Thread.sleep(1000)
        controller.cpuTurn()
      }
    }
  }
}