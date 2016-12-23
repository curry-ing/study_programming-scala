package objectsystem.linearization.v1

class C1 {
  print("C1-1 ")
  def m = print("C1 ")
}

trait T1 extends C1 {
  print("T1-1 ")
  override def m = { print("T1 "); super.m }
}

trait T2 extends C1 {
  print("T2-1 ")
  override def m = { print("T2 "); super.m }
}

trait T3 extends C1 {
  print("T3-1 ")
  override def m = { print("T3 "); super.m }
}

class C2 extends T1 with T2 with T3 {
  println("C2-1 ")
  override def m = { print("C2 "); super.m }
}

object Linearization1 {
  def main(args: Array[String]): Unit = {
    val c2 = new C2
    c2.m
  }
}
