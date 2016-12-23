val res = 1 +: Seq(2, 3)

val b = 0.1 +: res

class Upper
class Middle1 extends Upper
class Middle2 extends Middle1
class Lower extends Middle2
case class C1[A >: Lower <: Upper](a: A)
case class C2[A <: Upper >: Lower](a: A)
