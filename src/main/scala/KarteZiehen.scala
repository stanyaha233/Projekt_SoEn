package uno
import scala.util.Random

object KarteZiehen {

  def ziehen(): Karte = {
    val alleFarben = Farbe.values.toList
    val alleZahlen = Zahl.values.toList

    val zufallsFarbe = Random.shuffle(alleFarben).head

    val gewaehlterWert = if (zufallsFarbe != Farbe.Schwarz) {
      Random.shuffle(alleZahlen.filterNot(z => z == Zahl.plus4 || z == Zahl.wahl)).head    } else {
      // Da deine Karte in Main.scala Zahl.Value erwartet,
      // müssen wir hier schauen: Deine Sonderkarten sind in einem
      // extra Enum. Das ist ein Problem für den Typ 'Karte'.
      Random.shuffle(List(Zahl.wahl, Zahl.plus4)).head
    }
    Karte(zufallsFarbe, gewaehlterWert)
  }
  def anfangskarten(hand: Hand): Hand = {
    var aktuelleHand = hand
    while (aktuelleHand.anzahl < 7) {
      aktuelleHand = aktuelleHand.add(ziehen())
    }
    aktuelleHand
  }
}