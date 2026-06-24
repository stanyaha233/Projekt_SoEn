package uno.gui

// $COVERAGE-OFF$
import com.google.inject.Inject
import scala.swing._
import scala.swing.event._
import uno.controller.ControllerInterface
import uno.util.Observer
import uno.model._
import java.awt.Color

private[gui] class CardPanel(color: Color, valueText: String) extends Component {
  private val cardWidth = 100
  private val cardHeight = 150
  preferredSize = new Dimension(cardWidth, cardHeight)
  maximumSize = new Dimension(cardWidth, cardHeight)

  override def paintComponent(g: Graphics2D): Unit = {
    g.setColor(color)
    g.fillRoundRect(0, 0, cardWidth, cardHeight, 20, 20)
    g.setColor(Color.WHITE)
    g.setStroke(new java.awt.BasicStroke(3))
    g.drawRoundRect(0, 0, cardWidth - 1, cardHeight - 1, 20, 20)
    g.setColor(Color.WHITE)
    val font = new java.awt.Font("Arial", java.awt.Font.BOLD, 12)
    g.setFont(font)
    val metrics = g.getFontMetrics(font)
    val textWidth = metrics.stringWidth(valueText)
    val x = (cardWidth - textWidth) / 2
    val y = (cardHeight - metrics.getHeight) / 2 + metrics.getAscent
    g.drawString(valueText, x, y)
  }
}

class SwingGui @Inject() (controller: ControllerInterface) extends Frame with Observer {
  controller.add(this)

  title = "Uno GUI"
  minimumSize = new Dimension(700, 450)

  private val cpuLabel = new Label("Gegner hat: " + controller.cpuHandCount + " Karten")
  private val statusLabel = new Label("Willkommen bei Uno!")
  private val colourLabel = new Label("Aktuelle Farbe: " + controller.activeColour)

  private var pilePanel: Component = new CardPanel(Color.GRAY, "Uno")

  private val drawButton = new Button("Karte ziehen") {
    reactions += { case ButtonClicked(_) => controller.drawCard() }
  }

  private val undoButton = new Button("Undo") {
    reactions += { case ButtonClicked(_) => controller.undo() }
  }

  private val redoButton = new Button("Redo") {
    reactions += { case ButtonClicked(_) => controller.redo() }
  }

  private val handPanel = new GridPanel(0, 4) { vGap = 5; hGap = 5 }
  private val centerPanel = new BoxPanel(Orientation.Vertical)

  contents = new BorderPanel {
    add(new BoxPanel(Orientation.Vertical) {
      contents += cpuLabel
      contents += statusLabel
    }, BorderPanel.Position.North)

    add(centerPanel, BorderPanel.Position.Center)
    add(new ScrollPane(handPanel), BorderPanel.Position.South)
  }

  private def getCardValueSymbol(value: Number.Value): String = value.toString match {
    case "zero"  => "0"; case "one"   => "1"; case "two"   => "2"
    case "three" => "3"; case "four"  => "4"; case "five"  => "5"
    case "six"   => "6"; case "seven" => "7"; case "eight" => "8"
    case "nine"  => "9"; case _ => value.toString
  }

  private def updateCenterPanel(): Unit = {
    centerPanel.contents.clear()
    val mainBox = new BoxPanel(Orientation.Vertical) {
      contents += Swing.VGlue
      contents += new BorderPanel {
        preferredSize = new Dimension(100, 150)
        maximumSize = new Dimension(100, 150)
        add(pilePanel, BorderPanel.Position.Center)
        xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      }
      colourLabel.xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      contents += colourLabel
      contents += Swing.VStrut(20)
      drawButton.xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      contents += drawButton
      undoButton.xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      contents += undoButton
      redoButton.xLayoutAlignment = java.awt.Component.CENTER_ALIGNMENT
      contents += redoButton
      contents += Swing.VGlue
    }
    centerPanel.contents += mainBox
    centerPanel.peer.revalidate()
    centerPanel.peer.repaint()
  }

  override def update(): Unit = {
    if (!controller.isGameActive) {
      handleGameOver(if (controller.playerHandCount == 0) "Du hast gewonnen!" else "Der Gegner hat gewonnen!")
    }

    statusLabel.text = controller.state.statusMessage
    cpuLabel.text = "Gegner hat: " + controller.cpuHandCount + " Karten"

    val pileCard = controller.pileCard
    pilePanel = new CardPanel(getColor(pileCard.colour.toString), getCardValueSymbol(pileCard.value))

    val activeColorName = controller.activeColour.toString
    colourLabel.text = "Aktuelle Farbe: " + activeColorName
    colourLabel.foreground = getColor(activeColorName)

    updateCenterPanel()

    handPanel.contents.clear()
    for (card <- controller.playerHandCards) {
      val btnColor = getColor(card.colour.toString)

      val buttonText = getCardValueSymbol(card.value)

      handPanel.contents += new Button(buttonText) {
        background = btnColor; foreground = Color.WHITE; opaque = true; borderPainted = true
        reactions += { case ButtonClicked(_) => handleCardClick(card) }
      }
    }
    this.peer.revalidate()
    this.peer.repaint()
  }

  private var selectionSource: () => Option[String] = () => {
    val options = List("Red", "Blue", "Green", "Yellow")
    Dialog.showInput(this, "Wähle eine Farbe:", "Wunschkarte", Dialog.Message.Question, null, options, "Red")
  }

  private def askForColour(): Colour.Value = mapSelectionToColour(selectionSource())
  private def mapSelectionToColour(selection: Option[String]): Colour.Value = selection match {
    case Some("Red") => Colour.Red; case Some("Blue") => Colour.Blue
    case Some("Green") => Colour.Green; case Some("Yellow") => Colour.Yellow
    case _ => Colour.Red
  }

  private def handleGameOver(msg: String): Unit = { Dialog.showMessage(this, msg, "Spiel vorbei"); this.dispose() }
  private def handleCardClick(card: Card): Unit = {
    if (card.colour == Colour.Black) controller.playCard(card, Some(askForColour()))
    else controller.playCard(card)
  }

  private def getColor(colorName: String): Color = colorName.toLowerCase match {
    case "red" => Color.RED; case "blue" => Color.BLUE
    case "green" => new Color(0, 150, 0); case "yellow" => new Color(200, 160, 0)
    case _ => Color.BLACK
  }

  controller.sortHandByColor()
  visible = true
}
// $COVERAGE-ON$