package uno

import Main.Karte
import Kartenset.Farbe
import Kartenset.Zahl
import Kartenset.Sonderkarten
import Handkarten.Hand


def karteziehen(): Unit {
    val zufallsFarbe: String = Random.shuffle(Farben).head
    val zufallsZahl : String
    if(zufallsFarbe != "schwarz"){
        zufallsZahl = Random.shuffle(Zahl).head
    }
    else {
        zufallsZahl = Random.shuffle(Sonderkarten).head
    }
    Hand().add(Karte(zufallsFarbe, zufallsZahl))

}

def anfangskarten(): Unit = {
  while (Hand.anzahl <= 7){
    karteziehen()
  }
}