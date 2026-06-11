package uno.gui

import uno.controller.UnoLogic
import uno.model._

class TestSwingGui(controller: UnoLogic) extends SwingGui(controller) {
  override def askForColour(): Colour.Value = Colour.Red
}