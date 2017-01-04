package typesystem.valuetypes

trait Logger {
  def log(message: String): Unit
}

class ConsoleLogger extends Logger {
  def log(message: String): Unit = println(s"log: $message")
}


trait Service {
  type Log <: Logger // Log는 Logger를 바운드하긴 하지만 아직 구체적인 타입이 결정되지는 않음
  val logger: Log
}

class Service1 extends Service {
  type Log = ConsoleLogger
  val logger: ConsoleLogger = new ConsoleLogger
}
