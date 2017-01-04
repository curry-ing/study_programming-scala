package typesystem.higherkinded

import scala.language.higherKinds

trait Reduce[T, -M[T]] {
  def reduce(m: M[T])(f: (T, T) => T): T
}

object Reduce {
  implicit def seqReduce[T] = new Reduce[T, Seq] {
    def reduce(seq: Seq[T])(f: (T, T) => T): T = seq reduce f
  }

  implicit def optionReduce[T] = new Reduce[T, Option] {
    def reduce(opt: Option[T])(f: (T, T) => T): T = opt reduce f
  }
}
