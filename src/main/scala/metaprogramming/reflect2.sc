import scala.language.existentials

trait T[A] {
  val vT: A
  def mT = vT
}

class C(foo: Int) extends T[String] {
  val vT = "T"
  val vC = "C"
  val mC = vC

  class C2
}

val c = new C(3)  // c: C = metaprogramming.A$A13$A$A13$C@4e46fafe

c.isInstanceOf[String]    // res0: Boolean = false
// Warning:(18, 16) fruitless type test: a value of type A$A13.this.C cannot also be a String (the underlying of String)

c.isInstanceOf[C] // res1: Boolean = true

c.asInstanceOf[T[AnyRef]] // res2: T[AnyRef] = metaprogramming.A$A13$A$A13$C@4e46fafe