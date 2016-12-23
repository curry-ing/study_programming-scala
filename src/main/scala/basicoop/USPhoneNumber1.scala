package basicoop

class USPhoneNumber1(val s: String) {
  override def toString = {
    val digs = digits(s)
    val areaCode = digs.substring(0, 3)
    val exchange = digs.substring(3, 6)
    val subnumber = digs.substring(6, 10)
    s"($areaCode)$exchange-$subnumber"
  }

  private def digits(str: String): String = str.replaceAll("""\D""", "")
}

object USPhoneNumber1 {
  val number = new USPhoneNumber1("987-654-3210")

  def print(n: USPhoneNumber1) = n.toString

  print(number)
}