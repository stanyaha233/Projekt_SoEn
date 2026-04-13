package uno

import Main.Karte

class Hand(val karten: List[Karte] = Nil) {
  def add(karte: Karte): Hand = new Hand(karten :+ karte)
  def anzahl: Int = karten.size
}