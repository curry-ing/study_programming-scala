package fp.categories

trait Monad[M[_]] {
  def flatMap[A, B](fa: M[A])(f: A => M[B]): M[B]
  def unit[A](a: => A): M[A]

  def bind[A, B](fa: M[A])(f: A => M[B]): M[B] = flatMap(fa)(f)
  def >>=[A, B](fa: M[A])(f: A => M[B]): M[B] = flatMap(fa)(f)
  def pure[A](a: => A): M[A] = unit(a)
  def `return`[A](a: => A): M[A] = unit(a)
}

object SeqM extends Monad[Seq] {
  def flatMap[A, B](seq: Seq[A])(f: A => Seq[B]): Seq[B] = seq flatMap f
  def unit[A](a: => A): Seq[A] = Seq(a)
}

object OptionM extends Monad[Option] {
  def flatMap[A, B](opt: Option[A])(f: A => Option[B]): Option[B] = opt flatMap f
  def unit[A](a: => A): Option[A] = Option(a)
}
