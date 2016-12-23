class Parent(val value: Int) {  // 1. 보여주기 위한 간단한 타입 계층을 정의
override def toString = s"${this.getClass.getName}($value)"
}
class Child(value: Int) extends Parent(value)

case class Opt[A](value: A = null) { // Opt[+A]
  def getOrElse(default: A) = if (value != null) value else default
}

//val p4: Parent = Opt(new Child(1)).getOrElse(new Parent(10))

val p5: Parent = Opt[Parent](null).getOrElse(new Parent(10))

//val p6: Parent = Opt[Child](null).getOrElse(new Parent(10))
