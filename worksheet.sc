
object Colour extends scala.Enumeration {
  val Red, Green, Blue, Yellow, Black = Value
}

object Number extends Enumeration {
  val zero, one, two, three, four, five, six, seven, eight, nine, directionchange, plus2, skip, plus4, choice = Value
}

case class Karte(farbe: String, wert: String)


val meineZahl = Number.one
val meineFarbe = Colour.Red

val meineKarte = Karte(meineFarbe.toString, meineZahl.toString)

val karte1 = "Rot 7"
val karte2 = "Blau Skip"
val karte3 = "Grün 3"
val karte4 = "Schwarz Wahl"
val karte5 = "Schwarz Plus4"
val karte6 = "Blau plus2"
val karte7 = "Grün Wechsel"

val hand = List(karte1, karte2, "Grün 2")
val handGegner = List(karte6, karte5, karte4, karte7)

println(s"Zahl: $meineZahl")
println(s"Farbe: $meineFarbe")
println(s"Mitte ist $karte1")
println(s"Ich habe ${hand.size} Karten auf der Hand.")
println(s"Der Gegner hat ${handGegner.size} Karten auf der Hand.")