import scala.reflect.ClassTag

def mkArray[T: ClassTag](elems: T*) = Array[T](elems: _*) // mkArray: mkArray[T](val elems: T*)(implicit <synthetic> val evidence$2: scala.reflect.ClassTag[T]) => Array[T]

mkArray(1, 2, 3)  // res0: Array[Int] = Array(1, 2, 3)

mkArray("one", "two", "three")  // res1: Array[String] = Array(one, two, three)

mkArray(1, "two", 3.14) // res2: Array[Any] = (1, two, 3.14)
// warning: a type was inferred to by `Any`; this may indicate a programming error.

