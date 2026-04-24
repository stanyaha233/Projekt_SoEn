package uno

class Hand(val cards: List[Card]) {
  def add(card: Card): Hand = new Hand(cards :+ card)
  def count: Int = cards.size
  def possible(middle: Card): Boolean = cards.exists(k => k.colour == middle.colour || k.value == middle.value || k.colour == Colour.Black)
}