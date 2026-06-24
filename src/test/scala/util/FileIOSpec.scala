package uno.util

import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import scala.io.Source

class FileIOSpec extends AnyFunSuite {
  private val testData = Map("player" -> "Alice", "score" -> "100", "level" -> "5")
  private val xmlFile = "test_data.xml"
  private val jsonFile = "test_data.json"

  test("FileIOXml should save and load data") {
    val fileIO = new FileIOXml
    fileIO.save(testData, xmlFile)
    val loaded = fileIO.load(xmlFile)
    assert(loaded == testData)
    new File(xmlFile).delete()
  }

  test("FileIOJson should save and load data") {
    val fileIO = new FileIOJson
    fileIO.save(testData, jsonFile)
    val loaded = fileIO.load(jsonFile)
    assert(loaded == testData)
    new File(jsonFile).delete()
  }

  test("XML file should have correct format") {
    val fileIO = new FileIOXml
    fileIO.save(testData, xmlFile)
    val content = Source.fromFile(xmlFile).mkString
    assert(content.contains("<data>"))
    assert(content.contains("<entry"))
    assert(content.contains("</data>"))
    new File(xmlFile).delete()
  }

  test("JSON file should have correct format") {
    val fileIO = new FileIOJson
    fileIO.save(testData, jsonFile)
    val content = Source.fromFile(jsonFile).mkString
    assert(content.contains("\"player\""))
    assert(content.contains("\"Alice\""))
    new File(jsonFile).delete()
  }
}
