package uno

import Main.Karte
import Kartenset.Farbe
import Kartenset.Zahl
import Kartenset.Sonderkarten
import Handkarten.Hand


def karteziehen(): Unit {
    val zufallsFarbe = Random.shuffle(Farben).head
    val zufallsZahl
    if(zufallsFarbe != "schwarz"){
        zufallsZahl = Random.shuffle(Zahl).head
    }
    else {
        zufallsZahl = Random.shuffle(Sonderkarten).head
    }
    val neueListe = Hand.meinekarten :+ Karte(zufallsFarbe, zufallsZahl)
    Hand.meinekarten = neueListe

}

def anfangskarten(): Unit = {
  while (Hand.anzahl <= 7){
    karteziehen()
  }
}