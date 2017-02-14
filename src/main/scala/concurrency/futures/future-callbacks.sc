import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class ThatsOdd(i: Int) extends RuntimeException(s"odd $i received!") // 홀수를 받는경우 예외

import scala.util.{Try, Success, Failure}

val doComplete: PartialFunction[Try[String], Unit] = {  // 성공과 실패를 한번에 처리할 콜백 함수
                                                        // 콜백에 성공과 실패를 모두 캡슐화 가능한 Try[A] 타입 (여기서 A는 String)
                                                        // 콜백 처리 함수는 비동기적으로 호출되기에 아무것도 반환하지 않기에 Unit 타입
  case s @ Success(_) => println(s)
  case f @ Failure(_) => println(f)
}

val futures = (0 to 9) map {    // Future 동반객체의 두 메서드를 사용하여 Future를 즉시 실패나 성공으로 완료시킬 수 있음
  case i if (i % 2) == 0 => Future.successful(i.toString)
  case i => Future.failed(ThatsOdd(i))
}

futures map (_ onComplete doComplete) // futures를 순회하며 콜백을 연결, 이 시점에 모든 Future가 완료되었기 때문에 콜백도 즉시 호출됨
