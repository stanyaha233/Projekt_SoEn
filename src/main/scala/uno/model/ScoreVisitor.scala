package uno.model

class ScoreVisitor extends Visitor {
  var score: Int = 0

  override def visit(card: Card): Unit = {
    score += (card.value match {
      case Number.plus4 | Number.choice => 20
      case Number.plus2 | Number.skip | Number.directionchange => 10
      case _ => 5
    })
  }
}
