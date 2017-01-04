package typesystem.higherkinded

import scala.language.higherKinds

trait Reduce1[-M[_]] {
  def reduce[T](m: M[T])(f: (T, T) => T): T
}

object Reduce1 {
  implicit val seqReduce = new Reduce1[Seq] {
    def reduce[T](seq: Seq[T])(f: (T, T) => T): T = seq reduce f
  }

  implicit def optionReduce[T] = new Reduce1[Option] {
    def reduce[T](opt: Option[T])(f: (T, T) => T): T = opt reduce f
  }
}
