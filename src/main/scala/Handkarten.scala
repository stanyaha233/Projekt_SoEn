package uno

class Hand(val karten: List[Karte]) {
  def add(karte: Karte): Hand = new Hand(karten :+ karte)
  def anzahl: Int = karten.size
  def moeglich(mitte: Karte): Boolean = karten.exists(k => k.farbe == mitte.farbe || k.wert == mitte.wert || k.farbe == Farbe.Schwarz)
}