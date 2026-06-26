package uno.model

trait Visitor {
  def visit(card: Card): Unit
}