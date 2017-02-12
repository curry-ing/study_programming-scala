package concurrency.akka

import akka.actor.{ActorRef, ActorSystem}
import java.lang.{NumberFormatException => NFE}

object AkkaClient {     // AkkaClient가 객체이므로 main정의 가능

  import Messages._

  private var system: Option[ActorSystem] = None    // ActorSystem 하나를 Option에 배치 -> 종료 로직에서 사용
                                                    // 변경 가능하나(var) 액터가 모든 제어권을 가지기에 동시 접근 걱정 불필요

  def main(args: Array[String]) = {
    processArgs(args)                     // 도움말 기능 제공 (정상인 경우 아무 처리하지 않음)
    val sys = ActorSystem("AkkaClient")   // ActorSystem 생성 & Option인 system 갱신
    system = Some(sys)
    val server = ServerActor.make(sys)    // ServerActor의 동반객체에 있는 메서드 호출해서 ServerActor 인스턴스 생성
    val numberOfWorkers = sys.settings.config.getInt("server.number-workers")
    server ! Start(numberOfWorkers)       // Start 메시지를 ServerActor에 전달해 처리 시작
    processInput(server)                  // 사용자가 cmd에 입력한 내용 처리
  }

  private def processArgs(args: Seq[String]): Unit = args match {
    case Nil =>
    case ("-h" | "--help") +: tail => exit(help, 0)
    case head +: tail => exit(s"Unknown input $head!\n" + help, 1)
  }

  private def processInput(server: ActorRef): Unit = {
    val blankRE = """^\s*#?\s*$""".r
    val badCrashRE = """^\s*[Cc][Rr][Aa][Ss][Hh]\s*$""".r
    val crashRE = """^\s*[Cc][Rr][Aa][Ss][Hh]\s+(\d+)\s*$""".r
    val dumpRE = """^\s*[Dd][Uu][Mm][Pp](\s+\d+)?\s*$""".r
    val charNumberRE = """^\s*(\w)\s+(\d+)\s*$""".r
    val charNumberStringRE = """^\s*(\w)\s+(\d+)\s+(.*)$""".r   // 입력을 위한 정규 표현식 정의

    def prompt() = print(">> ")
    def missingActorNumber() = println("Crash command requires an actor number.")
    def invalidInput(s: String) = println(s"Unrecognized command: $s")
    def invalidCommand(c: String) = println(s"Expected 'c', 'r', 'u', or 'd'. Got $c")
    def invalidNumber(s: String) = println(s"Expected a number. Got $s")
    def expectedString() = println("Expected a string after the command and number")
    def unexpectedString(c: String, n: Int) = println(s"Extra arguments after command and number '$c $n'")
    def finished(): Nothing = exit("Goodbye!", 0)    // 프롬프트 출력, 오류 표시, 처리종료 후 프로그램을 닫기 위한 여러 내포 메서드를 정의

    val handleLine: PartialFunction[String, Unit] = { // 주 처리부, 부분함수를 사용한다
      case blankRE() => /* do nothing */              // 빈 줄이거나 주석인 행을 무시
      case "h" | "help" => println(help)              // 도움말 요청 검사
      case dumpRE(n) => server ! (if (n == null) DumpAll else Dump(n.trim.toInt))  // 상태를 덤프하라고 하나 혹은 모든 작업 액터에 요청
                                                                                   // 각각의 상태는 key-value의 data store
      case badCrashRE() => missingActorNumber()    //
      case crashRE(n) => server ! Crash(n.toInt)
      case charNumberStringRE(c, n, s) => c match {     // 명령에 글자 & 수 & 문자열이 모두 들어온 경우 처리
        case "c" | "C" => server ! Create(n.toInt, s)   // 생성
        case "u" | "U" => server ! Update(n.toInt, s)   // 갱신
        case "r" | "R" => unexpectedString(c, n.toInt)  // 기타 무시
        case "d" | "D" => unexpectedString(c, n.toInt)
        case _ => invalidCommand(c)
      }
      case charNumberRE(c, n) => c match {          // 명령에 문자 & 수 만 있는 경우 처리
        case "r" | "R" => server ! Read(n.toInt)    // 읽기
        case "d" | "D" => server ! Delete(n.toInt)  // 삭제
        case "c" | "C" => expectedString            // 기타 무시
        case "u" | "U" => expectedString
        case _ => invalidCommand(c)
      }
      case "q" | "quit" | "exit" => finished()    // 어플리케이션을 종료하는 세가지 방법
      case string => invalidInput(string)         // 기타 다른 입력은 모두 에러 처리
    }

    while (true) {
      prompt()
      Console.in.readLine() match {
        case null => finished()
        case line => handleLine(line)
      }
    }
  }

  private val help =
    """Usage: AkkaClient [-h | --help]
      |Then, enter one of the following commands, one per line:
      | h | help    Print this help message.
      | c n string  Create "record" for key n for value string.
      | rn          Read record for key n.It 's an error if n isn 't found.
      | u n string  Update(or create) record for key n for value string.
      | dn          Delete record for key n.It 's an error if n isn 't found.
      | crash n     "Crash" worker n(to test recovery).
      | dump [n]    Dump the state of all workers (default) or worker n.
      | ^d | quit   Quit.
      |""".stripMargin    // 출력될 도움말

  private def exit(message: String, status: Int): Nothing = { // 어플리케이션 종료를 위한 도우미 함수
    for (sys <- system) sys.shutdown()  // system 이 Some인 경우 시스템 정지
    println(message)
    sys.exit(status)
  }
}
