package uno

import uno.{Karte, Farbe, Zahl}


object UnoSpiel extends App { // Der Name des Objekts ist frei wählbar
    // Hier kommt dein Code rein
    var hand = KarteZiehen.anfangskarten(new Hand(List.empty[Karte]))
    var gegnerhand = KarteZiehen.anfangskarten(new Hand(List.empty[Karte]))
    val mitte = KarteZiehen.ziehen()
    val moeglich = hand.karten.exists(k => k.farbe == mitte.farbe || k.wert == mitte.wert || k.farbe == Farbe.Schwarz)

    println("Uno")
    while (hand.anzahl > 0 && gegnerhand.anzahl > 0) {
        println("Stapel: " + mitte.farbe + " " + mitte.wert)

        println("Deine Hand:")
        println(hand.karten.map(k => s"${k.farbe} ${k.wert}").mkString(", "))

        println("Gegner Hand Anzahl: " + gegnerhand.anzahl)

        println("Möglichkeit zu einem Zug: " + moeglich)
        println(" ")
        val input = scala.io.StdIn.readLine("Welche Karte möchtest du legen? (Farbe Nummer)")
    }
}