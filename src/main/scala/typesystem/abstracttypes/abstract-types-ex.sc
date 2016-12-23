trait exampleTrait {
  type t1
  type t2 >: t3 <: t1
  type t3 <: t1
//  type t3 <: t2
  type t4 <: Seq[t1]
  // type t5 = +AnyRef

  val v1: t1
  val v2: t2
  val v3: t3
  val t4: t4
}