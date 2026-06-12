package uno.model

trait SortingStrategy:
  def sort(hand: List[Card]): List[Card]

class SortByColorStrategy extends SortingStrategy:
  def sort(hand: List[Card]): List[Card] =
    hand.sortBy(card => (card.colour, card.value))

class SortByValueStrategy extends SortingStrategy:
  def sort(hand: List[Card]): List[Card] =
    hand.sortBy(card => (card.value, card.colour))
