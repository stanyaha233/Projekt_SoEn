package uno

class Hand(val cards: List[Card]) {
  def add(card: Card): Hand = new Hand(cards :+ card)
  def count: Int = cards.size
  def possible(middle: Card, activeColour: Colour.Value): Boolean = cards.exists(k => k.colour == middle.colour || k.value == middle.value || k.colour == Colour.Black)
}