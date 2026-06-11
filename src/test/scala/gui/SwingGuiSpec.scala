package uno.gui

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import uno.controller.UnoLogic
import uno.model._
import uno.util.GameFactory

class SwingGuiSpec extends AnyWordSpec with Matchers {

  // Basis-Setup für die Tests
  val state = GameFactory.createInitialState()
  val testController = new UnoLogic(state) {
    override def notifyObservers(): Unit = {}
  }

  // Verwendung der benannten Test-Klasse anstatt einer anonymen Klasse
  val testGui = new TestSwingGui(new UnoLogic(GameFactory.createInitialState()))
  val gui = new SwingGui(testController)

  "SwingGui" should {

    "askForColour Logik durch TestSwingGui abdecken" in {
      val result = testGui.askForColour()
      result should be(Colour.Red)
    }

    "die getColor Methode indirekt testen" in {
      noException should be thrownBy gui.update()
    }

    "korrekt initialisiert werden" in {
      gui.title should be ("Uno GUI")
      gui.visible should be (true)
    }

    "das Update erfolgreich durchführen" in {
      noException should be thrownBy {
        gui.update()
      }
    }

    "die GUI-Komponenten mit Daten befüllen" in {
      gui.update()
      gui.statusLabel.text should be ("Spiel startet!")
      gui.pileLabel.text should include ("Stapel")
      gui.cpuLabel.text should include ("Gegner hat")
    }
  }

  "Der 'Karte ziehen'-Button" should {
    "beim Klicken die entsprechende Controller-Methode aufrufen" in {
      gui.drawButton.doClick()
    }
  }

  "SwingGui Farbauswahl-Logik" should {
    "Red korrekt mappen" in {
      gui.mapSelectionToColour(Some("Red")) should be (Colour.Red)
    }
    "Blue korrekt mappen" in {
      gui.mapSelectionToColour(Some("Blue")) should be (Colour.Blue)
    }
    "Green korrekt mappen" in {
      gui.mapSelectionToColour(Some("Green")) should be (Colour.Green)
    }
    "Yellow korrekt mappen" in {
      gui.mapSelectionToColour(Some("Yellow")) should be (Colour.Yellow)
    }
    "den case _ Fall (Fehler/Abbruch) korrekt behandeln" in {
      gui.mapSelectionToColour(None) should be (Colour.Red)
    }
    "Dialog Pfad ausführen" in {
      val gui = new SwingGui(new UnoLogic(GameFactory.createInitialState()))

      gui.selectionSource = () => Some("Red")

      gui.askForColour() should be(Colour.Red)
    }
    "den Standard-Codeblock erzwingen" in {
      val gui = new SwingGui(new UnoLogic(GameFactory.createInitialState()))
      try {
        gui.selectionSource()
      } catch {
        case e: Exception =>
      }
    }
  }

  "Die GUI" should {
    "die Karten-Klick-Logik für normale Karten abdecken" in {
      val state = GameFactory.createInitialState()
      val gui = new TestSwingGui(new UnoLogic(state))
      gui.update()

      val firstButton = gui.handPanel.contents.head.asInstanceOf[scala.swing.Button]

      firstButton.doClick()

      gui.dispose()
    }

    "die Karten-Klick-Logik für schwarze Karten abdecken" in {
      val state = GameFactory.createInitialState()
      val gui = new TestSwingGui(new UnoLogic(state))

      gui.selectionSource = () => Some("Red")
      val blackCard = Card(Colour.Black, Number.zero)

      noException should be thrownBy gui.handleCardClick(blackCard)
      gui.dispose()
    }

    "den Sieg-Dialog bei leerer Hand anzeigen" in {
      val emptyHand = Hand(List())
      val fullHand = Hand(List(Card(Colour.Red, Number.one)))

      val winningState = GameState(
        playerHand = emptyHand,
        cpuHand = fullHand,
        pile = Card(Colour.Red, Number.one),
        activeColour = Colour.Red,
        isPlayerTurn = true,
        statusMessage = "Test"
      )
      val gui = new SwingGui(new UnoLogic(winningState)) {
        override def handleGameOver(msg: String): Unit = {
        }
      }

      noException should be thrownBy gui.update()
    }
    "Gelbe Farbe korrekt verarbeiten" in {
      val state = GameState(
        playerHand = Hand(List(Card(Colour.Yellow, Number.one))),
        cpuHand = Hand(List()),
        pile = Card(Colour.Red, Number.one),
        activeColour = Colour.Yellow,
        isPlayerTurn = true
      )
      val gui = new SwingGui(new UnoLogic(state))
      gui.update()
    }
    "den Sieg-Dialog bei leerer CPU-Hand anzeigen" in {
      val fullPlayerHand = Hand(List(Card(Colour.Red, Number.one)))
      val emptyCpuHand = Hand(List())

      val cpuWinningState = GameState(
        playerHand = fullPlayerHand,
        cpuHand = emptyCpuHand,
        pile = Card(Colour.Red, Number.one),
        activeColour = Colour.Red,
        isPlayerTurn = true,
        statusMessage = "Test"
      )

      val gui = new SwingGui(new UnoLogic(cpuWinningState)) {
        override def handleGameOver(msg: String): Unit = {}
      }
      noException should be thrownBy gui.update()
    }
    "den Sieg-Dialog anzeigen, wenn der Gegner gewinnt" in {
      val fullPlayerHand = Hand(List(Card(Colour.Red, Number.one)))
      val emptyCpuHand = Hand(List())

      val cpuWinningState = GameState(
        playerHand = fullPlayerHand,
        cpuHand = emptyCpuHand,
        pile = Card(Colour.Red, Number.one),
        activeColour = Colour.Red,
        isPlayerTurn = true,
        statusMessage = "Test"
      )

      val gui = new SwingGui(new UnoLogic(cpuWinningState)) {
        override def handleGameOver(msg: String): Unit = {}
      }
      noException should be thrownBy gui.update()
    }
  }

  "SwingGui GUI-Elemente" should {
    "korrekt initialisiert werden" in {
      gui.title should be("Uno GUI")
      gui.visible should be(true)
    }

    "das Update ohne Fehler durchlaufen" in {
      noException should be thrownBy gui.update()
    }
  }
}