package uno

object Colour extends Enumeration {
    val Red, Green, Blue, Yellow, Black = Value
  }

  object Number extends Enumeration {
    val zero, one, two, three, four, five, six, seven, eight, nine, directionchange,plus2, skip, plus4, choice = Value
  }

object ConsoleColors {
  val RESET  = "\u001B[0m"
  val RED    = "\u001B[31m"
  val GREEN  = "\u001B[32m"
  val BLUE   = "\u001B[34m"
  val YELLOW = "\u001B[33m"
  val WHITE  = "\u001B[37m"
}

def formatCard(card: Card): String = {
  val colorCode = card.colour match {
    case Colour.Red    => ConsoleColors.RED
    case Colour.Green  => ConsoleColors.GREEN
    case Colour.Blue   => ConsoleColors.BLUE
    case Colour.Yellow => ConsoleColors.YELLOW
    case Colour.Black  => ConsoleColors.WHITE
  }
  s"$colorCode[${card.colour} ${card.value}]${ConsoleColors.RESET}"
}