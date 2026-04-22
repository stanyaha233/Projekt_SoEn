package uno

class Hand(val karten: List[Karte]) {
  def add(karte: Karte): Hand = new Hand(karten :+ karte)
  def anzahl: Int = karten.size
}