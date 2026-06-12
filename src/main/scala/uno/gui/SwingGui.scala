package uno.gui

import scala.swing._
import scala.swing.event._
import uno.controller.UnoLogic
import uno.util.Observer
import uno.model._
import java.awt.Color

class SwingGui(controller: UnoLogic) extends Frame with Observer {
  controller.add(this)

  title = "Uno GUI"
  minimumSize = new Dimension(700, 450)

  val cpuLabel = new Label("Gegner hat: " + controller.state.cpuHand.count + " Karten")
  val statusLabel = new Label("Willkommen bei Uno!")
  val pileLabel = new Label("Stapel: " + controller.state.pile)
  val colourLabel = new Label("Aktuelle Farbe: " + controller.state.activeColour)

  val drawButton = new Button("Karte ziehen") {
    reactions += { case ButtonClicked(_) => controller.drawCard() }
  }

  val handPanel = new GridPanel(0, 4) { vGap = 5; hGap = 5 }

  contents = new BorderPanel {
    add(new BoxPanel(Orientation.Vertical) {
      contents += cpuLabel
      contents += statusLabel
    }, BorderPanel.Position.North)

    val centerPanel = new BoxPanel(Orientation.Horizontal) {
      contents += Swing.HGlue
      contents += new BoxPanel(Orientation.Vertical) {
        contents += pileLabel
        contents += colourLabel
        contents += Swing.VStrut(20)
        contents += drawButton
      }
      contents += Swing.HGlue
    }
    add(centerPanel, BorderPanel.Position.Center)
    add(new ScrollPane(handPanel), BorderPanel.Position.South)
  }

  var selectionSource: () => Option[String] = () => {
    val options = List("Red", "Blue", "Green", "Yellow")
    Dialog.showInput(this, "Wähle eine Farbe:", "Wunschkarte",
      Dialog.Message.Question, null, options, "Red")
  }

  def askForColour(): Colour.Value = {
    val selection = selectionSource()
    mapSelectionToColour(selection)
  }

  def mapSelectionToColour(selection: Option[String]): Colour.Value = selection match {
    case Some("Red")    => Colour.Red
    case Some("Blue")   => Colour.Blue
    case Some("Green")  => Colour.Green
    case Some("Yellow") => Colour.Yellow
    case _              => Colour.Red
  }

  def handleGameOver(msg: String): Unit = {
    Dialog.showMessage(this, msg, "Spiel vorbei")
    this.dispose()
  }

  def handleCardClick(card: Card): Unit = {
    if (card.colour == Colour.Black) {
      controller.playCard(card, Some(askForColour()))
    } else {
      controller.playCard(card)
    }
  }

  private def getColor(colorName: String): Color = colorName.toLowerCase match {
    case "red"    => Color.RED
    case "blue"   => Color.BLUE
    case "green"  => new Color(0, 150, 0)
    case "yellow" => new Color(200, 160, 0)
    case _        => Color.BLACK
  }

  override def update(): Unit = {
    if (controller.state.playerHand.cards.isEmpty || controller.state.cpuHand.cards.isEmpty) {
      val msg = if (controller.state.playerHand.cards.isEmpty) "Du hast gewonnen!" else "Der Gegner hat gewonnen!"
      handleGameOver(msg)
    }

    statusLabel.text = controller.state.statusMessage
    pileLabel.text = "Stapel: " + controller.state.pile
    cpuLabel.text = "Gegner hat: " + controller.state.cpuHand.count + " Karten"

    val activeColorName = controller.state.activeColour.toString
    colourLabel.text = "Aktuelle Farbe: " + activeColorName
    colourLabel.foreground = getColor(activeColorName)

    handPanel.contents.clear()
    for (card <- controller.state.playerHand.cards) {
      val btnColor = getColor(card.colour.toString)
      val button = new Button() {
        text = s"${card.colour} ${card.value}"
        background = btnColor
        foreground = Color.WHITE
        opaque = true
        borderPainted = true

        reactions += {
          case ButtonClicked(_) => handleCardClick(card)
        }
      }
      handPanel.contents += button
    }
    handPanel.peer.revalidate()
    this.peer.revalidate()
    this.repaint()
  }

  controller.sortHandByColor()

  visible = true
}