package uno.model

case class Card(colour: Colour.Value, value: Number.Value) {
  def accept(visitor: Visitor): Unit = visitor.visit(this)
}
