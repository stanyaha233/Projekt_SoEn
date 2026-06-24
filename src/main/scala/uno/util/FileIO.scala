package uno.util

trait FileIO {
  def save(data: Map[String, String], filename: String): Unit
  def load(filename: String): Map[String, String]
}
