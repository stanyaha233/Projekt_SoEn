package uno.util

object FileIOUsageExample {
  def main(args: Array[String]): Unit = {
    val gameData = Map(
      "playerName" -> "Alice",
      "playerScore" -> "1500",
      "cpuName" -> "Bot",
      "cpuScore" -> "1200"
    )

    val xmlFileIO = new FileIOXml
    xmlFileIO.save(gameData, "game_data.xml")
    val loadedXml = xmlFileIO.load("game_data.xml")
    println("Loaded from XML: " + loadedXml)
    val jsonFileIO = new FileIOJson
    jsonFileIO.save(gameData, "game_data.json")
    val loadedJson = jsonFileIO.load("game_data.json")
    println("Loaded from JSON: " + loadedJson)
  }
}
