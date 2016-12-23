package basicoop

class Test1(val n: Int)

object Test1 {
  def get(t: Test1) = t.n
  val v = new Test1(10)
  get(v) == 10
}
