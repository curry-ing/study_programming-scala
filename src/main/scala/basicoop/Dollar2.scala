package basicoop

class Dollar2(val value: Float) {
  override def toString = "$%.2f".format(value)
}

object Dollar2 {
  def benjamin(d: Dollar2) = d.toString
}