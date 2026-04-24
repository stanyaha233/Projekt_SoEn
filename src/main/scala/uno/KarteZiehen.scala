package uno
import scala.util.Random

object Draw {

  def draw(): Card = {
    val allColours = Colour.values.toList
    val allNumbers = Number.values.toList

    val randomColour = Random.shuffle(allColours).head

    val chosenValue = if (randomColour != Colour.Black) {
      Random.shuffle(allNumbers.filterNot(z => z == Number.plus4 || z == Number.choice)).head    } else {
      Random.shuffle(List(Number.choice, Number.plus4)).head
    }
    Card(randomColour, chosenValue)
  }
  def beginningHand(hand: Hand): Hand = {
    var currentHand = hand
    while (currentHand.count < 7) {
      currentHand = currentHand.add(draw())
    }
    currentHand
  }
}