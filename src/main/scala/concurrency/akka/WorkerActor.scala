package concurrency.akka

import akka.actor.{Actor, ActorLogging}

import scala.util.{Success, Try}

class WorkerActor extends Actor with ActorLogging {
  import Messages._

  private val datastore = collection.mutable.Map.empty[Long, String]

  def receive = {
    case Create(key, value) =>
      datastore += key -> value
      sender ! Response(Success(s"$key -> $value added"))
    case Read(key) =>
      sender ! Response(Try(s"${datastore(key)} found for key = $key"))
    case Update(key, value) =>
      datastore += key -> value
      sender ! Response(Success(s"$key -> $value updated"))
    case Delete(key) =>
      datastore -= key
      sender ! Response(Success(s"$key deleted"))
    case Crash(_) => throw WorkerActor.CrashException
    case DumpAll =>
      sender ! Response(Success(s"${self.path}: datastore = $datastore"))
  }
}

object WorkerActor {
  case object CrashException extends RuntimeException("Crash")
}
