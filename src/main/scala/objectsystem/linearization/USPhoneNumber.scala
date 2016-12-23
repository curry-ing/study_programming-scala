package objectsystem.linearization

trait M extends Any {
  def m = print("M ")
}

trait Digitizer extends Any with M {
  override def m = { print("Digitizer "); super.m }

  def digits(s: String): String = s.replaceAll("""\D""", "")
}


trait Formatter extends Any with M {
  override def m = { print("Formatter "); super.m }

  def format(areaCode: String, exchange: String, subnumber: String): String =
    s"($areaCode) $exchange - $subnumber"
}

class USPhoneNumber(val s: String) extends AnyVal with Digitizer with Formatter {
  override def m = { print("USPhoneNumber "); super.m }

  override def toString = {
    val digs = digits(s)
    val areaCode = digs.substring(0,3)
    val exchange = digs.substring(3,6)
    val subnumber = digs.substring(6,10)
    format(areaCode, exchange, subnumber)
  }
}

object USPhoneNumber {
  def main(args: Array[String]): Unit = {
    val number = new USPhoneNumber("987-654-3210")
    print("Call m: ")
    number.m
  }
}
