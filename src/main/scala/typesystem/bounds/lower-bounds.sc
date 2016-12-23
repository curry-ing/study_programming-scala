class Parent(val value: Int) {  // 1. 보여주기 위한 간단한 타입 계층을 정의
  override def toString = s"${this.getClass.getName}($value)"
}
class Child(value: Int) extends Parent(value)

val op1: Option[Parent] = Option(new Child(1))    // 2.
val p1: Parent = op1.getOrElse(new Parent(10))

val op2: Option[Parent] = Option[Parent](null)    // 3.
val p2a: Parent = op2.getOrElse(new Parent(10))
val p2b: Parent = op2.getOrElse(new Child(100))

val op3: Option[Parent] = Option[Child](null)     // 4.
val p3a: Parent = op3.getOrElse(new Parent(20))
val p3b: Parent = op3.getOrElse(new Child(200))

