package uno.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class UndoManagerSpec extends AnyWordSpec with Matchers {
  "An UndoManager" should {
    val undoManager = new UndoManager

    "undo a command" in {
      var value = "initial"
      val command = new Command {
        override def execute(): Unit = value = "changed"
        override def undo(): Unit = value = "initial"
        override def redo(): Unit = value = "changed"
      }

      undoManager.executeCommand(command)
      value should be("changed")

      undoManager.undo()
      value should be("initial")
    }

    "not crash when undoing on an empty stack" in {
      val emptyUndoManager = new UndoManager()
      emptyUndoManager.undo()
    }
  }
}