package concurrency.akka

import akka.actor.{Actor, ActorLogging}

import scala.util.{Success, Try}

class WorkerActor extends Actor with ActorLogging {
  import Messages._

  private val datastore = collection.mutable.Map.empty[Long, String]  // mutable.
                                                                      // Receive 처리기는 thread-safe & 변경 가능 상태를 액터 자시만 볼 수 있기에 mutable을 사용해도 안전
                                                                      // 이를 메시지를 통해 메시지 송신자와 공유하는 일은 결코 없다 (private)
  def receive = {
    case Create(key, value) =>
      datastore += key -> value
      sender ! Response(Success(s"$key -> $value added"))
    case Read(key) =>
      sender ! Response(Try(s"${datastore(key)} found for key = $key")) // Try: 키가 없을 때 발생하는 예외를 자동으로 Failure로, 존재하면 Success로 wrapping
    case Update(key, value) =>
      datastore += key -> value
      sender ! Response(Success(s"$key -> $value updated"))
    case Delete(key) =>
      datastore -= key
      sender ! Response(Success(s"$key deleted"))
    case Crash(_) => throw WorkerActor.CrashException   // 본 예외 발생 시 해당 액터를 재시작 (Strategy)
    case DumpAll =>
      sender ! Response(Success(s"${self.path}: datastore = $datastore"))   // 액터의 상태를 반환 (datastore맵에 있는 내용을 가지고 만든 문자열)
  }
}

object WorkerActor {
  case object CrashException extends RuntimeException("Crash")    // 시뮬레이션을 위한 코드
}
