package objectsystem.ui

abstract class Widget {
  def draw(): Unit
  override def toString = "(widget)"
}

