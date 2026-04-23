package uno

case class Card(colour: Colour.Value, value: Number.Value)


def main(): Unit = {

  val middle = Card(Colour.Red, Number.seven)

  val card1 = Card(Colour.Blue, Number.three)
  val card2 = Card(Colour.Green, Number.Skip)
  val card3 = Card(Colour.Red,Number.two)
  val card4 = Card(Colour.Black, Number.choice)
  val card5 = Card(Colour.Black,Number.plus4)
  val card6 = Card(Colour.Blue, Number.plus2)
  val card7 = Card(Colour.Green, Number.directionChange)

  val hand = List(card1, card2, card3)
  val handGegner = List(card6, card5, card4, card7)

  println("Uno")
  println("Stapel: " + middle.colour + " " + middle.value)
  println(" ")
   println("Gegner Hand Anzahl:" + handGegner.size)

  println("Deine Hand:")
  println("Karte 1: " + card1.colour + " " + card1.value)
  println("Karte 2: " + card2.colour + " " + card2.value)
  println("Karte 3: " + card3.colour + " " + card3.value)

  val moeglich = hand.exists(k => k.colour == middle.colour || k.value == middle.value || k.colour == Colour.Black)

  println(" ")
  println("Möglichkeit zu einem Zug: " + moeglich)
}
