package uno.model
import scala.util.Random

object DeckFactory {
  def createStandardDeck(): List[Card] = {
    var deck = List[Card]()
    val normalColours = Colour.values.filterNot(_ == Colour.Black)
    val normalNumbers = Number.values.filterNot(n => n == Number.plus4 || n == Number.choice)

    // Alle normalen Karten (Farben + Zahlen/Aktionskarten) zum Deck hinzufügen
    for {
      c <- normalColours
      n <- normalNumbers
      _ <- 1 to 2 // In Uno gibt es fast jede Karte doppelt
    } deck = Card(c, n) :: deck

    // Die schwarzen Aktionskarten (je 4 Stück)
    for (_ <- 1 to 4) {
      deck = Card(Colour.Black, Number.choice) :: deck
      deck = Card(Colour.Black, Number.plus4) :: deck
    }

    Random.shuffle(deck) // Komplettes Deck mischen
  }
}

object Draw {
  // Der aktuelle Nachziehstapel, der von der Factory bereitgestellt wird
  var drawPile: List[Card] = DeckFactory.createStandardDeck()

  def draw(): Card = {
    // Wenn der Stapel leer ist, erstellt die Fabrik einfach einen neuen gemischten Stapel
    if (drawPile.isEmpty) {
      drawPile = DeckFactory.createStandardDeck()
    }
    // Oberste Karte vom Stapel nehmen und aus der Liste entfernen
    val card = drawPile.head
    drawPile = drawPile.tail
    card
  }

  def beginningHand(hand: Hand): Hand = {
    var currentHand = hand
    while (currentHand.count < 7) {
      currentHand = currentHand.add(draw())
    }
    currentHand
  }
}