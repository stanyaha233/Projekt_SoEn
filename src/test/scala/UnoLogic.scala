package uno

import uno.{Karte, Farbe, Zahl}

class UnoLogicSpec extends munit.FunSuite {

  // Testdaten vorbereiten
  val rote7 = Karte(Farbe.Rot, Zahl.sieben)
  val rote9 = Karte(Farbe.Rot, Zahl.neun)
  val blaue7 = Karte(Farbe.Blau, Zahl.sieben)

  test("Eine Karte sollte auf die gleiche Farbe gelegt werden können") {
    val hand = new Hand(List(rote7))
    val moeglich = hand.moeglich(rote9)
    assertEquals(moeglich, true)
  }

  test("Eine Karte sollte auf den gleichen Wert gelegt werden können") {
    val hand = new Hand(List(rote7))
    val moeglich = hand.moeglich(blaue7)
    assertEquals(moeglich, true)
  }

  test("Handanzahl sollte sich nach dem Hinzufügen erhöhen") {
    val hand = new Hand(List(rote7))
    val neueHand = hand.add(rote9)
    assertEquals(neueHand.anzahl, 2)
  }
}