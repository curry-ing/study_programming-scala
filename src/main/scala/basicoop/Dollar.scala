package basicoop

class Dollar(val value: Float) extends AnyVal {
  override def toString = "$%.2f".format(value)
}

object Dollar {
  def benjamin(d: Dollar) = d.toString
}
