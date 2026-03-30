@main
def main(): Unit = {
  case class Karte(farbe: String, wert: String)

  val mitte = Karte("Rot", "7")
  val karte1 = Karte("Blau", "3")
  val karte2 = Karte("Grün", "Skip")
  val karte3 = Karte("Rot", "3")
  val karte4 = Karte("Schwarz", "Wahl")
  val karte5 = Karte("Schwarz","Plus4")
  val karte6 = Karte("Blau", "plus2")
  val karte7 = Karte("Grün", "Wechsel")

  val hand = List(karte1, karte2, karte3)
  val handGegner = List(karte6, karte5, karte4, karte7)

  println("Uno")
  println("Stapel: " + mitte.farbe + " " + mitte.wert)
  println(" ")
  println("Gegner Hand Anzahl:" + handGegner.size)

  println("Deine Hand:")
  println("Karte 1: " + karte1.farbe + " " + karte1.wert)
  println("Karte 2: " + karte2.farbe + " " + karte2.wert)
  println("Karte 3: " + karte3.farbe + " " + karte3.wert)

  val moeglich: Boolean = hand.exists(karte => (karte.farbe == mitte.farbe)|| (karte.wert == mitte.wert) || karte.farbe == "schwarz")

  println(" ")
  println("Möglichkeit zu einem Zug: " + moeglich)
}

