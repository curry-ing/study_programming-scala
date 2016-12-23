package traits.ui

class ButtonWithCallbacks(val label: String, val callbacks: List[() => Unit] = Nil) extends Widget {
  def click(): Unit = {
    updateUI()
    callbacks.foreach(f => f())
  }

  protected def updateUI(): Unit = { /* do Something */ }
}

object ButtonWithCallbacks {
  def apply(label: String, callback: () => Unit) =
    new ButtonWithCallbacks(label, List(callback))

  def apply(label: String) =
    new ButtonWithCallbacks(label, Nil)
}