package concurrency.akka

import scala.util.Try

object Messages {         // 모든 메시지 타입을 담아둠
  sealed trait Request {  // 모든 CRUD 요청에 대한 부모 트레이트
    val key: Long         // 모든 요청은 Long 키를 사용
  }
  case class Create(key: Long, value: String) extends Request
  case class Read(key: Long) extends Request
  case class Update(key: Long, value: String) extends Request
  case class Delete(key: Long) extends Requ​est

  case class Response(result: Try[String])  // 응답을 공통 메시지로 감싸서 반환
                                            // scala.util.Try는 결과를 성공/실패 여부와 함께 감싼다

  case class Start(numberOfWor​kers: Int = 1)  // 처리 시작, 이 메시지를 ServerActor에 보내면서
                                              // 얼마나 많은 작업 액터를 생성(numberOfWorkers)할 지 지정
  case class Crash(whichOne: Int) // 작업 액터가 '심각한 문제'를 시뮬레이션하도록 한다
  case class Dump(whichOne: Int)  // 어느 한 작업 액터나 모든 액터의 상태를 '덤프'
  case object DumpAll
}
