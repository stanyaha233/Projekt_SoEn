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
        println("Stapel: " + middle.colour + " " + middle.value)
        println(" ")
        println("Gegner Hand Anzahl: " + opponentCard.count)
        println(" ")
        println("Möglichkeit zu einem Zug: " + hand.possible(middle))
        println(" ")
        if (turn) {
            if (middle.value == Number.plus2) {
                println("Du musst 2 Karten ziehen!")
                hand = hand.add(Draw.draw()).add(Draw.draw())
                turn = false
            }
            else if (middle.value == Number.plus4) {
                println("Du musst 4 Karten ziehen!")
                hand = hand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw())
                turn = false
            }
            else if (middle.value == Number.Skip) {
                println("Der Gegner überspringt deinen Zug!")
                turn = false
            } else if (middle.value == Number.directionChange) {
                println("Richtungswechsel! Du darfst nochmal!")
                turn = true
            }
            println(" ")
            if(hand.possible(middle)) {
                println("Deine Hand:")
        println(hand.cards.map(k => s"${k.colour} ${k.value}").mkString(", "))
        println(" ")
            val input = scala.io.StdIn.readLine("Welche Karte möchtest du legen? (Farbe Nummer)")
            val Array(farbeInput, wertInput) = input.split(" ")
            val chosenColour = Colour.withName(farbeInput)
            val chosenValue = Number.withName(wertInput)
            if (chosenValue == middle.value || chosenColour == middle.colour || chosenColour == Colour.Black) {
                println(s"Du hast die Karte ${chosenColour} ${chosenValue} gelegt.")
                hand = new Hand(hand.cards.filterNot(k => k.colour == chosenColour && k.value == chosenValue))
                middle = Card(chosenColour, chosenValue)
                turn = false
            } else {
            println("Ungültiger Zug. Bitte versuche es erneut.")
            println(" ")
            }
            } else {
            println("Du musst eine Karte ziehen.")
            hand = hand.add(Draw.draw())
            turn = false
            }
            if (hand.count == 0) {
            println("Herzlichen Glückwunsch! Du hast gewonnen!")
            }
        }
         else {
            if (middle.value == Number.plus2) {
                println("Du musst 2 Karten ziehen!")
                hand = hand.add(Draw.draw()).add(Draw.draw())
                turn = true
            }
            else if (middle.value == Number.plus4) {
                println("Du musst 4 Karten ziehen!")
                hand = hand.add(Draw.draw()).add(Draw.draw()).add(Draw.draw()).add(Draw.draw())
                turn = true
            }
            else if (middle.value == Number.Skip) {
                println("Der Gegner überspringt deinen Zug!")
                turn = true
            } else if (middle.value == Number.directionChange) {
                println("Richtungswechsel! Du darfst nochmal!")
                turn = false
            }
            if(opponentCard.possible(middle)) {
                val chosenCard = opponentCard.cards.find(k => k.colour == middle.colour || k.value == middle.value || k.colour == Colour.Black)
                if (chosenCard != None) {
                    val card = chosenCard.head
                    opponentCard = new Hand(opponentCard.cards.filterNot(k => k.colour == card.colour && k.value == card.value))
                    middle = card
                    println(s"Der Gegner hat die Karte ${card.colour} ${card.value} gelegt.")
                }} else {
                println("Der Gegner muss eine Karte ziehen.")
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