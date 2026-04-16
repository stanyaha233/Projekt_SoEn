package uno

case class Karte(farbe: Farbe.Value, wert: Zahl.Value)

@main
def main(): Unit = {

  val mitte = Karte(Farbe.Rot, Zahl.sieben)

  val karte1 = Karte(Farbe.Blau, Zahl.drei)
  val karte2 = Karte(Farbe.Gruen, Zahl.Skip)
  val karte3 = Karte(Farbe.Rot,Zahl.zwei)
  val karte4 = Karte(Farbe.Schwarz, Zahl.wahl)
  val karte5 = Karte(Farbe.Schwarz,Zahl.plus4)
  val karte6 = Karte(Farbe.Blau, Zahl.plus2)
  val karte7 = Karte(Farbe.Gruen, Zahl.Wechsel)

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

  val moeglich = hand.exists(k => k.farbe == mitte.farbe || k.wert == mitte.wert || k.farbe == Farbe.Schwarz)

  println(" ")
  println("Möglichkeit zu einem Zug: " + moeglich)
}
