package concurrency.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

class ServerActor extends Actor with ActorLogging {
  import Messages._

  implicit val timeout = Timeout(1.seconds)

  override val supervisorStrategy: SupervisorStrategy = { // 기본 관리 전략을 전용 SupervisorStrategy로 오버라이딩.
    val decider: SupervisorStrategy.Decider = {
      case WorkerActor.CrashException => SupervisorStrategy.Restart // 액터에 심각한 문제 발생시 액터 재시작
      case NonFatal(ex) => SupervisorStrategy.Resume                // 다른 NonFatal에러시 계속 진행
    }
    OneForOneStrategy()(decider orElse super.supervisorStrategy.decider)  // 각각의 작업 액터가 독립적이라고 간주: one-for-one 적합
                                                                          // Decider에 오류가 없다면 처리를 부모 관리 액터에 위임
  }

  var workers = Vector.empty[ActorRef]  // 작업 액터를 참조하는 ActorRef를 통해 작업 액터 추적

  def receive = initial

  val initial: Receive = {  // Receive: Actor가 편의를 위해 제공하는 타입 멤버, `PartialFunction[Any, Unit]`에 대한 별명
                            // 액터가 최초로 받는 시작 메시지 처리를 위한 선언
                            // 다른 메시지가 도착하는 경우, 그 메시지를 명시적으로 처리하지 않는 한 우편함에 남는다
                            // 액터가 구성하는 상태 기계의 초기 상태로 간주
    case Start(numberOfWorkers) =>    // Start를 받는 경우만 처리
      workers = ((1 to numberOfWorkers) map makeWorker).toVector  // 작업 스레드 생성
      context become processRequests  // 메시지를 processRequests가 처리하는 상태 기계의 두 번째 상태로 천이
  }

  val processRequests: Receive = {  // Start가 도착한 다음부터 나머지 메시지 처리
    case c @ Crash(n) => workers(n % workers.size) ! c      // 사용자가 지정한 작업자 인덱스 n을
                                                            // 실제 작업자 개수(workers.size)로 나눈 나머지를 사용해 액터를 찾음
    case DumpAll =>    // DumpAll을 모든 작업자에 전달 후 응답을 종합해 결과 메시지를 생성 => 물어보기 패턴
                       // `!`(Unit을 반환) 대신 `?`를 사용해서 메시지를 보내면 `Future` 리턴
                       // 두 메시지 타입 모두 비동기적이나 물어보기 패턴에서는 메시지 수신자의 리턴값이 Future에 포획
      Future.fold(workers map (_ ? DumpAll))(Vector.empty[Any])(_ :+ _)  // fold: Future의 시퀀스를 Vector로 감싼 단일 Future로 통합
        .onComplete(askHandler("State of the workers"))                  // Future 완료를 처리할 콜백 askHandler를 등록
    case Dump(n) =>
      (workers(n % workers.size) ? DumpAll).map(Vector(_))
        .onComplete(askHandler(s"Statee of worker $n"))
    case request: Request =>        // CRUD 명령을 처리
      val key = request.key.toInt
      val index = key % workers.size
      workers(index) ! request
    case Response(Success(message)) => printResult(message)   // 작업자로부터 받은 Response 메시지 처리
    case Response(Failure(ex)) => printResult(s"ERROR! $ex")
  }

  def askHandler(prefix: String): PartialFunction[Try[Any], Unit] = {
    case Success(suc) => suc match {
      case vect: Vector[_] =>
        printResult(s"$prefix:\n")
        vect foreach {
          case Response(Success(message)) => printResult(s"$message")
          case Response(Failure(ex)) => printResult(s"ERROR! Success received wrapping $ex")
        }
      case _ => printResult(s"BUG! Expected a vector, got $suc")
    }
    case Failure(ex) => printResult(s"ERROR! $ex")
  }

  protected def printResult(message: String) = {
    println(s"<< $message")
  }

  protected def makeWorker(i: Int) = context.actorOf(Props[ServerActor], "server")
}

object ServerActor {
  def make(system: ActorSystem): ActorRef = system.actorOf(Props[ServerActor], "server")  // 액터의 간편한 생성을 도움
}
