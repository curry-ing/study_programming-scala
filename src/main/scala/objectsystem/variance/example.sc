List(1, 2, 3, 4) map (i => i + 3)

val f: Int => Int = new Function1[Int, Int] {
  def apply(i: Int): Int = i + 3
}

List(1, 2, 3, 4) map (f)
