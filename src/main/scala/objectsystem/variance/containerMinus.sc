//class ContainerMinus[-A](var value: A)

class ContainerMinus[-A](var a: A) {
  private var _value: A = a
  def value_=(newA: A): Unit = _value = newA
  def value: A = _value
}

import objectsystem.variance._

val cm: ContainerMinus[C] = new ContainerMinus(new CSuper)
val c: C      = cm.value
val c: CSuper = cm.value
val c: CSub   = cm.value