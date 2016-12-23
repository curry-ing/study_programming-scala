//class ContainerPlus[+A](var value: A)
//
class ContainerPlus[+A](var a: A) {
  private var _value: A = a
  def value_=(newA: A): Unit = _value = newA
  def value: A = _value
}

import objectsystem.variance._

val cp = new ContainerPlus(new C)
cp.value = new C
cp.value = new CSub
cp.value = new CSuper

val cp2: ContainerPlus[C] = new ContainerPlus(new CSub)
cp2.value = new C
cp2.value = new CSub
cp2.value = new CSuper

