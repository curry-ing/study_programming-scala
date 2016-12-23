package objectsystem.linearization.v2

class C1 {
  print("C1 ")
}

trait T1 extends C1 {
  print("T1 ")
}

trait T2 extends C1 {
  print("T2 ")
}

trait T3 extends C1 {
  print("T3 ")
}

class C2 extends T2 with T1 with T3 {
  println("C2 ")
}

object Linearization2 {
  def main(args: Array[String]): Unit = {
    val c2 = new C2
  }
}
