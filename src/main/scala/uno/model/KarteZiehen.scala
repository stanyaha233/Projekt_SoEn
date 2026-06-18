package uno.model
import scala.util.Random

trait DeckProvider {
  def createStandardDeck(): List[Card]
}

trait CardDrawer {
  def draw(): Card
  def beginningHand(hand: Hand): Hand
  def pileSize: Int
  def getDeck: List[Card]
  def setDeck(deck: List[Card]): Unit
}

object DeckFactory extends DeckProvider {
  override def createStandardDeck(): List[Card] = {
    val normalColours = Colour.values.filterNot(_ == Colour.Black).toList
    val nonZeroNumbers = Number.values.filterNot(n => n == Number.plus4 || n == Number.choice || n == Number.zero).toList

    val zeroCards = normalColours.map(c => Card(c, Number.zero))

    val normalCards = for {
      c <- normalColours
      n <- nonZeroNumbers
      _ <- 1 to 2
    } yield Card(c, n)

    val blackCards = List.fill(4)(List(Card(Colour.Black, Number.choice), Card(Colour.Black, Number.plus4))).flatten

    Random.shuffle(zeroCards ++ normalCards ++ blackCards)
  }
}

object Draw extends CardDrawer {
  private var drawPile: List[Card] = DeckFactory.createStandardDeck()

  override def getDeck: List[Card] = drawPile
  
  override def setDeck(deck: List[Card]): Unit = drawPile = deck
  
  override def pileSize: Int = drawPile.length

  override def draw(): Card = {
    if (drawPile.isEmpty) {
      drawPile = DeckFactory.createStandardDeck()
    }
    val card = drawPile.head
    drawPile = drawPile.tail
    card
  }

  override def beginningHand(hand: Hand): Hand = {
    var currentHand = hand
    while (currentHand.count < 7) {
      currentHand = currentHand.add(draw())
    }
    currentHand
  }
}