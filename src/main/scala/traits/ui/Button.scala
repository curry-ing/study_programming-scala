package traits.ui

class Button(val label: String) extends Widget {
  def click(): Unit = updateUI()

  def updateUI(): Unit = { /* GUI 모양을 변경하는 로직 */ }
}
