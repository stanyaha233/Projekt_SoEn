package uno.util

import play.api.libs.json._
import scala.io.Source
import java.io._

class FileIOJson extends FileIO {
  override def save(data: Map[String, String], filename: String): Unit = {
    val json = Json.toJson(data)
    val writer = new FileWriter(filename)
    writer.write(Json.prettyPrint(json))
    writer.close()
  }

  override def load(filename: String): Map[String, String] = {
    val source = Source.fromFile(filename)
    val content = source.mkString
    source.close()

    val json = Json.parse(content)
    json.as[Map[String, String]]
  }
}
