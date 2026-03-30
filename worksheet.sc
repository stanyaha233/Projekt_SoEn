val mitte = "Rot 7"


val karte1 = "Rot 7"
val karte2 = "Blau Skip"
val karte3 = "Grün 3"
val karte4 = "Schwarz Wahl"
val karte5 = "Schwarz Plus4"
val karte6 = "Blau plus2"
val karte7 = "Grün Wechsel"


println(karte1)
karte3

val hand = List(karte1, karte2, "Grün 2")
val handGegner = List(karte6, karte5, karte4, karte7)
val anzahl = hand.size
val anzahlGegner = handGegner.size

println(s"Ich habe $anzahl Karten auf der Hand.")
println(s"Der Gegner hat $anzahlGegner Karten auf der Hand.")