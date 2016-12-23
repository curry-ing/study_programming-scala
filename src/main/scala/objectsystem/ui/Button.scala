package objectsystem.ui

import traits.ui2.Clickable

class Button(val label: String) extends Widget with Clickable {
  def draw(): Unit = println(s"Drawing: $this")
  protected def updateUI(): Unit = println(s"$this clicked; updating UI")

  override def toString = s"(button: label = $label, ${super.toString()})"
  val a: Int = 1
}
