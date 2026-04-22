package uno

import uno.{Karte, Farbe, Zahl}


object UnoSpiel extends App { // Der Name des Objekts ist frei wählbar
    // Hier kommt dein Code rein
    var hand = KarteZiehen.anfangskarten(new Hand(List.empty[Karte]))
    var gegnerhand = KarteZiehen.anfangskarten(new Hand(List.empty[Karte]))
    var mitte = KarteZiehen.ziehen()
    var turn: Boolean = true // true = Spieler, false = Gegner
    println("Uno")
    while (hand.anzahl > 0 && gegnerhand.anzahl > 0) {
        println(" ")
        println("Stapel: " + mitte.farbe + " " + mitte.wert)
        println(" ")
        println("Gegner Hand Anzahl: " + gegnerhand.anzahl)
        println(" ")
        println("Möglichkeit zu einem Zug: " + hand.moeglich(mitte))
        println(" ")
        if (turn) {
            if (mitte.wert == Zahl.plus2) {
                println("Du musst 2 Karten ziehen!")
                hand = hand.add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen())
                turn = false
            }
            else if (mitte.wert == Zahl.plus4) {
                println("Du musst 4 Karten ziehen!")
                hand = hand.add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen())
                turn = false
            }
            else if (mitte.wert == Zahl.Skip) {
                println("Der Gegner überspringt deinen Zug!")
                turn = false
            } else if (mitte.wert == Zahl.Wechsel) {
                println("Richtungswechsel! Du darfst nochmal!")
                turn = true
            }
            println(" ")
            if(hand.moeglich(mitte)) {
                println("Deine Hand:")
        println(hand.karten.map(k => s"${k.farbe} ${k.wert}").mkString(", "))
        println(" ")
            val input = scala.io.StdIn.readLine("Welche Karte möchtest du legen? (Farbe Nummer)")
            val Array(farbeInput, wertInput) = input.split(" ")
            val gewaehlteFarbe = Farbe.withName(farbeInput)
            val gewaehlterWert = Zahl.withName(wertInput)
            if (gewaehlterWert == mitte.wert || gewaehlteFarbe == mitte.farbe || gewaehlteFarbe == Farbe.Schwarz) {
                println(s"Du hast die Karte ${gewaehlteFarbe} ${gewaehlterWert} gelegt.")
                hand = new Hand(hand.karten.filterNot(k => k.farbe == gewaehlteFarbe && k.wert == gewaehlterWert))
                mitte = Karte(gewaehlteFarbe, gewaehlterWert)
                turn = false
            } else {
            println("Ungültiger Zug. Bitte versuche es erneut.")
            println(" ")
            }
            } else {
            println("Du musst eine Karte ziehen.")
            hand = hand.add(KarteZiehen.ziehen())
            turn = false
            }
            if (hand.anzahl == 0) {
            println("Herzlichen Glückwunsch! Du hast gewonnen!")
            }
        }
         else {
            if (mitte.wert == Zahl.plus2) {
                println("Du musst 2 Karten ziehen!")
                hand = hand.add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen())
                turn = true
            }
            else if (mitte.wert == Zahl.plus4) {
                println("Du musst 4 Karten ziehen!")
                hand = hand.add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen()).add(KarteZiehen.ziehen())
                turn = true
            }
            else if (mitte.wert == Zahl.Skip) {
                println("Der Gegner überspringt deinen Zug!")
                turn = true
            } else if (mitte.wert == Zahl.Wechsel) {
                println("Richtungswechsel! Du darfst nochmal!")
                turn = false
            }
            if(gegnerhand.moeglich(mitte)) {
                val gewaehlteKarte = gegnerhand.karten.find(k => k.farbe == mitte.farbe || k.wert == mitte.wert || k.farbe == Farbe.Schwarz)
                if (gewaehlteKarte != None) {
                    val karte = gewaehlteKarte.head
                    gegnerhand = new Hand(gegnerhand.karten.filterNot(k => k.farbe == karte.farbe && k.wert == karte.wert))
                    mitte = karte
                    println(s"Der Gegner hat die Karte ${karte.farbe} ${karte.wert} gelegt.")
                }} else {
                println("Der Gegner muss eine Karte ziehen.")
                gegnerhand = gegnerhand.add(KarteZiehen.ziehen())
            }
            turn = true
            println(" ")
            if (gegnerhand.anzahl == 0) {
                println("Der Gegner hat gewonnen! Viel Glück beim nächsten Mal!") 
            }
        }
        println(" ")
    }
}