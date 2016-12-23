package traits.ui2

import traits.ui.Widget

class Button(val label: String) extends Widget with Clickable {
  protected def updateUI(): Unit = { /* do Something */ }
}
