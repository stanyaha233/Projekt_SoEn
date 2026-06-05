package uno.util

class UndoManager {
  private var undoStack: List[Command] = Nil

  def executeCommand(command: Command): Unit = {
    command.execute()
    undoStack = command :: undoStack
  }

  def undo(): Unit = {
    undoStack match {
      case Nil => // nichts
      case command :: stack =>
        command.undo()
        undoStack = stack
    }
  }
}