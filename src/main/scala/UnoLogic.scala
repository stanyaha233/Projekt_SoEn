package uno

import uno.{Card, Colour, Number}


object UnoSpiel extends App {
    var hand = Draw.beginningHand(new Hand(List.empty[Card]))
    var opponentCard = Draw.beginningHand(new Hand(List.empty[Card]))
    var middle = Draw.draw()
    var turn: Boolean = true // true = Spieler, false = Gegner
    println("Uno")
    while (hand.count > 0 && opponentCard.count > 0) {
        println(" ")
        println("Middle: " + middle.colour + " " + middle.value)
        println(" ")
        println("Opponent Hand: " + opponentCard.count)
        println(" ")
        println("Possible Move: " + hand.possible(middle))
        println(" ")
        if (turn) {
            if (middle.value == Number.plus2) {
                println("You have to draw 2 cards!")
                hand = hand.add(Draw.draw()).add(Draw.draw())
                turn = false
            }
            else if (middle.value == Number.plus4) {
                println("You have to draw 4 cards!")
                hand = hand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw())
                turn = false
            }
            else if (middle.value == Number.Skip) {
                println("The opponent skips your turn!")
                turn = false
            } else if (middle.value == Number.directionChange) {
                println("Direction change! You get another turn!")
                turn = true
            }
            println(" ")
            if(hand.possible(middle)) {
                println("Your Hand:")
        println(hand.cards.map(k => s"${k.colour} ${k.value}").mkString(", "))
        println(" ")
            val input = scala.io.StdIn.readLine("Which card do you want to play? (Colour Number)")
            val Array(farbeInput, wertInput) = input.split(" ")
            val chosenColour = Colour.withName(farbeInput)
            val chosenValue = Number.withName(wertInput)
            if (chosenValue == middle.value || chosenColour == middle.colour || chosenColour == Colour.Black) {
                println(s"You have played the card ${chosenColour} ${chosenValue}.")
                hand = new Hand(hand.cards.filterNot(k => k.colour == chosenColour && k.value == chosenValue))
                middle = Card(chosenColour, chosenValue)
                turn = false
            } else {
            println("Invalid move. Please try again.")
            println(" ")
            }
            } else {
            println("You have to draw a card.")
            hand = hand.add(Draw.draw())
            turn = false
            }
            if (hand.count == 0) {
            println("Congratulations! You have won!")
            }
        }
         else {
            if (middle.value == Number.plus2) {
                println("You have to draw 2 cards!")
                hand = hand.add(Draw.draw()).add(Draw.draw())
                turn = true
            }
            else if (middle.value == Number.plus4) {
                println("You have to draw 4 cards!")
                hand = hand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw())
                turn = true
            }
            else if (middle.value == Number.Skip) {
                println("The opponent skips your turn!")
                turn = true
            } else if (middle.value == Number.directionChange) {
                println("Direction change! You get another turn!")
                turn = false
            }
            if(opponentCard.possible(middle)) {
                val chosenCard = opponentCard.cards.find(k => k.colour == middle.colour || k.value == middle.value || k.colour == Colour.Black)
                if (chosenCard != None) {
                    val card = chosenCard.head
                    opponentCard = new Hand(opponentCard.cards.filterNot(k => k.colour == card.colour && k.value == card.value))
                    middle = card
                    println(s"The opponent has played the card ${card.colour} ${card.value}.")
                }} else {
                println("The opponent has to draw a card.")
                opponentCard = opponentCard.add(Draw.draw())
            }
            turn = true
            println(" ")
            if (opponentCard.count == 0) {
                println("Der Gegner hat gewonnen! Viel Glück beim nächsten Mal!") 
            }
        }
        println(" ")
    }
}