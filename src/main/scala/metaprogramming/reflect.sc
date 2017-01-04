import scala.language.existentials

trait T[A] {
  val vT: A
  def mT = vT
}

class C(foo:Int) extends T[String] {
  val vT = "T"
  val vC = "C"
  val mC = vC

  class C2
}

val c = new C(3)    // c: C = metaprogramming.A$A39$A$A39$C@2096a4d8

val clazz = classOf[C]  // clazz: Class[C] = class metaprogramming.A$A41$A$A41$C

val clazz2 = c.getClass // clazz2: Class[?0] = class metaprogramming.A$A47$A$A47$C

val name = clazz.getName  // name: String = metaprogramming.A$A54$A$A54$C

val methods = clazz.getMethods  // methods: Array[java.lang.reflect.Method] = [Ljava.lang.reflect.Method;@566024e6

val ctors = clazz.getConstructors // ctors: Array[java.lang.reflect.Constructor[_]] = [Ljava.lang.reflect.Constructor;@18df33e8

val fields = clazz.getFields  // fields: Array[java.lang.reflect.Field] = [Ljava.lang.reflect.Field;@437f01f9

val annos = clazz.getAnnotations  // annos: Array[java.lang.annotation.Annotation] = [Ljava.lang.annotation.Annotation;@9b94891

val parentInterfaces = clazz.getInterfaces  // parentInterfaces: Array[Class[_]] = [Ljava.lang.Class;@1ca8e09d

val superClass = clazz.getSuperclass  // superClass: Class[?0] = class java.lang.Object

val typeParams = clazz.getTypeParameters  // typeParams: Array[java.lang.reflect.TypeVariable[Class[C]]] = [Ljava.lang.reflect.TypeVariable;@5600f5f9
