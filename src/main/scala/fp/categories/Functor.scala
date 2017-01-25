package fp.categories

import scala.language.higherKinds

trait Functor[F[_]] {   // `F`(아마 컨테이너의 일종일 것)의 인스턴스를 인자로 받음
  def map[A, B](fa: F[A])(f: A => B): F[B]  // `map`의 인자는 `F[A]`라는 펑터로 `A => B`로 변환하는 함수 `F[B]`를 리턴
}

object SeqF extends Functor[Seq] {    // Seq와 Option에 대한 구현 객체를 정의
  def map[A, B](seq: Seq[A])(f: A => B): Seq[B] = seq map f
}

object OptionF extends Functor[Option] {
  def map[A, B](opt: Option[A])(f: A => B): Option[B] = opt map f
}

object FunctionF {    // FunctionF는 자신만의 map 메서드를 정의
                      // map을 호출하는 구문이 Seq, Option, 그리고 우리가 구현할 수 있는 다른 변환에 있는 함수를 호출하는 것과 동일해지도록 구현
                      // 이 map은 변환할 대상 초기 함수를 받아 그 변환을 수행하는 함수를 반환 (타입에 유의)
                      // `A => A2`를 `A => B`로 변환: 함수 `f`가 `A2 => B`라는 뜻. => 함수를 연쇄시킨다
  def map[A, A2, B](func: A => A2)(f: A2 => B): A => B = {  // 변환을 수행하기 위해 올바른 타입이 지정된 Functor를 구성한다
    val functor = new Functor[({type L[b] = A => b})#L ] {  // `FunctorF.map`이 그 Functor를 호출. FunctionF.map의 반환 타입은 `A => B`다
      def map[A3, B](func: A => A3)(f: A3 => B): A => B = (a: A) => f(func(a))
    }
    functor.map(func)(f)
  }
}
