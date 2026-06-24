package uno.util

import scala.xml._
import scala.io.Source
import java.io._

class FileIOXml extends FileIO {
  override def save(data: Map[String, String], filename: String): Unit = {
    val xml: Elem = <data>
      {data.map { case (key, value) => <entry key={key}>{value}</entry> }.toSeq}
    </data>
    val writer = new FileWriter(filename)
    writer.write(xml.toString)
    writer.close()
  }

  override def load(filename: String): Map[String, String] = {
    val source = Source.fromFile(filename)
    val content = source.mkString
    source.close()

    val xml = XML.loadString(content)
    (xml \ "entry").map { node =>
      val key = (node \ "@key").text
      val value = node.text
      key -> value
    }.toMap
  }
}
