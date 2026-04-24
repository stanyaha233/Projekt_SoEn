package uno
import scala.io.StdIn

@main def startUno(): Unit = {
  println("=== Willkommen zu UNO ===")
  val firstCard = Draw.draw()
  val startState = GameState(
    playerHand = Draw.beginningHand(Hand(Nil)),
    cpuHand = Draw.beginningHand(Hand(Nil)),
    pile = firstCard,
    activeColour = if (firstCard.colour == Colour.Black) Colour.Red else firstCard.colour,
    isPlayerTurn = true
  )
  gameLoop(startState)
}

def gameLoop(state: GameState): Unit = {
  println(s"\n--- ${state.statusMessage} ---")
  println(s"Aktuelle Farbe: ${state.activeColour} | Karte auf Stapel: ${state.pile.colour} ${state.pile.value}")
  println(s"Gegner Karten: ${state.cpuHand.count}")

  if (state.playerHand.count == 0) return println("GLÜCKWUNSCH! Du hast gewonnen!")
  if (state.cpuHand.count == 0) return println("SCHADE! Der Gegner hat gewonnen!")

  if (state.isPlayerTurn) {
    println(s"Deine Hand: ${state.playerHand.cards.map(c => s"[${c.colour} ${c.value}]").mkString(", ")}")

    if (!state.playerHand.possible(state.pile)) {
      println("Du kannst nicht legen. Tippe 'draw' zum Ziehen.")
    }

    val input = StdIn.readLine("Zug (Farbe Wert) oder 'draw': ").trim.toLowerCase

    val nextState = if (input == "draw") {
      UnoLogic.drawCard(state)
    } else {
      parseInput(input, state)
    }
    gameLoop(nextState)
  } else {
    Thread.sleep(1500)
    gameLoop(UnoLogic.cpuTurn(state))
  }
}

def parseInput(input: String, state: GameState): GameState = {
  try {
    val parts = input.split(" ")
    if (parts.length < 2) throw new Exception()

    val col = Colour.withName(parts(0).capitalize)
    val valNum = Number.withName(parts(1))

    state.playerHand.cards.find(c => c.colour == col && c.value == valNum) match {
      case Some(c) =>
        if (c.colour == Colour.Black) {
          println("Wähle Farbe (Red, Green, Blue, Yellow):")
          val chosen = Colour.withName(StdIn.readLine().capitalize)
          UnoLogic.playCard(state, c, Some(chosen))
        } else {
          UnoLogic.playCard(state, c)
        }
      case None => state.copy(statusMessage = "Diese Karte hast du nicht!")
    }
  } catch {
    case _: Exception => state.copy(statusMessage = "Eingabe falsch! Nutze: 'Red seven' oder 'Black plus4'")
  }
}