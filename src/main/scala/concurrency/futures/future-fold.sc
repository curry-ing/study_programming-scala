import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

val futures = (0 to 9) map {  // 열 개의 비동기 퓨처를 생성, 각각은 소정의 작업을 수행
  
  i => Future {               // Future.apply는 인자 목록을 둘 받음
                              // 첫 번째: 비동기적으로 실행할 이름에 의한 호출 본문
                              // 두 번째: 암시적인 ExecutionContext가 들어감 (global)
                              // 타입: IndexedSeq[Future[String]]
    val s = i.toString        // 본문은 정수를 문자열로 변환하고, 그 문자열을 출력한 다음에 반환
    print(s)
    s
  }
}

val f = Future.reduce(futures)((s1, s2) => s1 + s2)   // Future 인스턴스의 시퀀스를 단일 `Future[String]`으로 축약

val n = Await.result(f, Duration.Inf)   // Future f 가 완료될 때 까지 블록하기 위해 Await를 사용
                                        // Duration 인자는 필요한 경우 '무한정' 기다리라고 지시
                                        // 어떤 Future가 작업을 완료할 때 까지 기다리는 경우 Await사용 방식이 선호됨
