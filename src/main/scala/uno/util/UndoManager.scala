package uno.util

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def executeCommand(command: Command): Unit = {
    command.execute()
    undoStack = command :: undoStack
    redoStack = Nil
  }

  def undo(): Unit = {
    undoStack match {
      case Nil =>
      case command :: stack =>
        command.undo()
        undoStack = stack
        redoStack = command :: redoStack
    }
  }

  def redo(): Unit = {
    redoStack match {
      case Nil =>
      case command :: stack =>
        command.redo()
        redoStack = stack
        undoStack = command :: undoStack 
    }
  }
}
