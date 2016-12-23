package basicoop

class Test2(val n: Int) extends AnyVal

object Test2 {
  def get(t: Test2) = t.n
  val v = new Test2(10)
  get(v) == 10
}
