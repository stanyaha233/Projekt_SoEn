package uno

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KarteSpec extends AnyFlatSpec with Matchers {

  "A Karte" should "properly store its color and value" in {
    val card = Karte(Farbe.Rot, Zahl.sieben)
    card.farbe should be(Farbe.Rot)
    card.wert should be(Zahl.sieben)
  }

  "Move logic" should "allow a move if the color matches" in {
    val topCard = Karte(Farbe.Rot, Zahl.sieben)
    val handCard = Karte(Farbe.Rot, Zahl.drei)

    val isPossible = handCard.farbe == topCard.farbe || handCard.wert == topCard.wert
    isPossible should be(true)
  }

  it should "allow a move if the value matches" in {
    val topCard = Karte(Farbe.Rot, Zahl.sieben)
    val handCard = Karte(Farbe.Blau, Zahl.sieben)

    val isPossible = handCard.farbe == topCard.farbe || handCard.wert == topCard.wert
    isPossible should be(true)
  }

  it should "deny a move if neither color nor value matches" in {
    val topCard = Karte(Farbe.Rot, Zahl.sieben)
    val handCard = Karte(Farbe.Blau, Zahl.drei)

    val isPossible = handCard.farbe == topCard.farbe || handCard.wert == topCard.wert
    isPossible should be(false)
  }

  it should "detect at least one playable card in a hand" in {
    val topCard = Karte(Farbe.Rot, Zahl.sieben)
    val hand = List(
      Karte(Farbe.Blau, Zahl.drei),
      Karte(Farbe.Gruen, Zahl.Skip),
      Karte(Farbe.Rot, Zahl.zwei)
    )

    val possible = hand.exists(k => k.farbe == topCard.farbe || k.wert == topCard.wert)
    possible should be(true)
  }

  it should "handle special black cards correctly (Wild cards)" in {
    val topCard = Karte(Farbe.Rot, Zahl.sieben)
    val wildCard = Karte(Farbe.Schwarz, Zahl.wahl)
    val possible = wildCard.farbe == topCard.farbe || wildCard.wert == topCard.wert || wildCard.farbe == Farbe.Schwarz

    possible should be(false)
  }
}