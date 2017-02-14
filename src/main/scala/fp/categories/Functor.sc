import fp.categories._
import scala.language.higherKinds

val fii: Int => Int       = i => i * 2
val fid: Int => Double    = i => 2.1 * i
val fds: Double => String = d => d.toString

SeqF.map(List(1,2,3,4))(fii)
SeqF.map(List.empty[Int])(fii)

OptionF.map(Some(2))(fii)
OptionF.map(Option.empty[Int])(fii)

val fa = FunctionF.map(fid)(fds)      // `Int => Double`과 `Double => String` 함수를 함께 연쇄해서 새로운 함수를 생성
fa(2)

// val fb = FunctionF.map(fid)(fds)     // 인자 타입 추론은 불가능. 함수 리터럴이나 `FunctionF.map`에 명시적으로 타입을 지정해야 한다
val fb = FunctionF.map[Int, Double, String](fid)(fds)
fb(2)

val fc = fds compose fid              // `FunctionF.map(f1)(f2) == f2 compose f1` (`f1 compose f2`가 아님)
fc(2)

def map[A, B](seq: Seq[A])(f: A => B): Seq[B] = seq map f

def map[A, B](f: A => B)(seq: Seq[A]): Seq[B] = seq map f

val fm = map((i: Int) => i * 2.1) _
