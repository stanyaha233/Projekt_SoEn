package uno.util

// $COVERAGE-OFF$
/**
 * Example showing how to use FileIO with dependency injection
 */
object FileIOUsageExample {
  def main(args: Array[String]): Unit = {
    // Example data to save
    val gameData = Map(
      "playerName" -> "Alice",
      "playerScore" -> "1500",
      "cpuName" -> "Bot",
      "cpuScore" -> "1200"
    )

    // Using XML implementation directly
    val xmlFileIO = new FileIOXml
    xmlFileIO.save(gameData, "game_data.xml")
    val loadedXml = xmlFileIO.load("game_data.xml")
    println("Loaded from XML: " + loadedXml)

    // Using JSON implementation directly
    val jsonFileIO = new FileIOJson
    jsonFileIO.save(gameData, "game_data.json")
    val loadedJson = jsonFileIO.load("game_data.json")
    println("Loaded from JSON: " + loadedJson)

    // With Guice (in real application), the implementation is injected
    // Example:
    // val injector = Guice.createInjector(new UnoModule())
    // val fileIO = injector.getInstance(classOf[FileIO])
    // fileIO.save(gameData, "game_data.xml")  // or .json, depends on binding
  }
}
// $COVERAGE-ON$
