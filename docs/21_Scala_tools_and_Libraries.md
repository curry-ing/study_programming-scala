# 21 스칼라 도구와 라이브러리

> **학습목표**
- `scalac` 컴파일러, `scala` REPL
- build tool, IDE, editors
- 스칼라 테스트 라이브러리
- 3rd-party scala libraries

## 21.1 명령행 도구
- IDE, SBT Repl에 익숙하더라도 여러 상황에 대한 유연한 대처 가능
- GUI툴 사용이 불가능한 경우 유용한 대비책
- *1.2절 스칼라 설치하기* 참조: `SCALA_HOME`은 스칼라가 설치된 위치
- More info about command line tools: http://www.scala-lang.org/documentation/

### 21.1.1 scalac
- 스칼라 소스 파일을 컴파일해서 JVM의 클래스 파일을 생성
- java 명령을 감싸는 shell-script
- `java`명령에 스칼라 컴파일러의 `Main`객체를 전달
  - 스칼라 `JAR`파일을 `CLASSPATH`에 추가하고 몇몇 스칼라 관련 시스템 프로퍼티를 설정
- 실행: `scalac <option> <source-file>`

---
- 소스파일 이름이 해당 파일 내에 있는 공개 클래스의 이름과 일치하지 않아도 무방
  - 한 클래스 나에서 여러 공개 클래스를 정의 가능
  - 패키지 선언도 실제 디렉토리 구조와 일치하지 않아도 무방
- JVM의 제약으로 인해 각각의 타입에 대해 그 타입 이름으로 된 별도의 클래스 파일이 생성
  - 클래스 파일들은 각각의 패키지 선언에 대응하는 디렉터리에 포함

##### 옵션
- `-encoding UTF8`: 비-ASCII 문자를 이름에 사용하거나 비-ASCII 심벌을 사용하고 싶은 경우
- `-explaintypes`: 타입 오류에 대해 더 자세히 설명
- `-feature`: 선택 기능이 된 고급 기능 사용시 따로 활성화(`-language:<feature>`)하지 않은 경우 경고
  - `dynamics`: Dynamic 트레이트를 활성화
  - `postifxOps`: 후위 연산자 활성화 (ex> `100 toString`)
  - `reflectiveCalls`: 구조적 타입 사용
  - `implicitConversions`: 암시적 메서드와 멤버 정의 가능
  - `higherKinds`: 고계타입 활성화
  - `experimental`: 실제 상용 환경에서 시험해보지 않은 새로운 기능 포함 (현재는 매크로가 유일한 실험적 기능)
- `-X`: 자세한 진단 출력 제어, 컴파일러의 동작 미세 제어, 실험적인 확정이나 플러그인 사용 조정
  - `-Xcheckinit`: 필드를 초기화하지 않고 접근자를 사용하는 경우 예외 (11.1.4 트레이트의 필드 오버라이딩하기 참조)
  - `-Xdisable-assertions`: 단언문이나 가정문 비활성화
  - `-Xexperimental`: 실험적인 확장 기능을 활성화
  - `-Xfatal-warnings`: 경고가 있는 경우 컴파일을 실패로 처리
  - `-Xfuture`: *미래* 의 언어 기능(그런 기능이 있다면)을 사용하도록 활성화
  - `-Xlint`: 바람직한 추가 경고를 활성화
  - `-Xlog-implicit-conversions`: 암시적 변환 추가시마다 메시지를 출력
  - `-Xlog-implicits`: 일부 암시에 대한 사용 불가능 이유 출력
  - `-Xmain-class <path>`: JAR파일의 매니페스트에서 사용할 `Main-Class` 진입점 항목의 클래스 지정 `-d jar` 옵션을 사용하는 경우 유효
  - `-Xmigration:v`: v로 지정한 버전 이후로 변경된 스칼라 구성에 대해 경고를 표시한다
  - `-Xscript <obj>`: 소스파일을 스크립트로 취급하고 `main`메서드를 사용해 그 파일의 내용을 둘러쌈
    - 스크립트 파일을 일반 스칼라 소스 파일처럼 컴파일하고 싶은 경우에만 유용
    - 스크립트 시작시마다 반복적인 컴파일로 인한 부가 비용을 제거
  - `-Y`: 새로운 언어 기능을 구현하는 사람들이 사용할 수 있는 **비공개** 옵션에 대한 개요 표시

##### `-Xlint` 예제
```
$scala -Xlint
Welcome to Scala version 2.11.2 ...
...
scala> def hello = pritnln("hello")
<console>:7: warning: side-effecting nullary methods are discouraged:
  suggest defining as `def hello()` instead
    def hello = println("hello!")
        ^
hello: Unit
```
- `Unit`을 반환하는 함수는 부수 효과만(여기서는 출력) 수행
- 경고 이유: **인자가 없는**(nullary) 메서드는 부수 효과가 없는 함수에 대해 사용하는것이 일반적

> **TIP**: `-deprecation`, `-unchecked`, `-feature`, `-Xlint`등의 옵션 사용 적극 권장
- 일부 버그 방지 가능
- 쓸모없는 라이브러리 사용 방지


### 2.1.2 scala 명령행 도구
- `scala <option> [<script|class|object|jar> <argument>]`
- 옵션이 아닌 첫 번째 인자가 실행할 프로그램으로 인식
- 아무 실행파일도 지정하지 않으면  REPL을 시작
- 프로그램 지정시, 해당 프로그램 뒤에 오는 모든 인자는 args 배열을 통해 대상 프로그램에 전달

##### 옵션
- `-howtorun:방법`: `object`, `script`, `jar`, `guess` 중 어떤 방법으로 실행할지 지정(default: `guess`)
  - 미지정시: 대상 프로그램의 종류를 추론하여 실행
- `-i <file>`: REPL을 시작하기 전에 파일의 내용을 미리 읽어들임
  - 일단 REPL에 진입했다면, `:load <file>`로 파일을 읽을 수 있음
  - REPL 시작시마다 동일한 명령을 반복하여 실행하는 경우 유용
- `-e <string>`: 문자열을 REPL에서 입력한 것처럼 실행
- `-save`: 컴파일한 스크립트를 나중에 사용할 수 있게 JAR파일에 저장: 재컴파일에 드는 부가비용 감소
- `-nc`: 컴파일 데몬인 `fsc`를 실행하지 않음: 컴파일러를 재시작하는 데 드는 부가비용을 줄이기 위해 `fsc`를 자동으로 시작

##### REPL에서 사용 가능한 명령
- `:cp <path>`: JAR나 디렉토리를 classpath에 추가
- `:edit <line number>`: 과거 입력을 편집
- `:help [command]`: 이 요약이나 명령에 따른 구체적인 도움말 표시
- `:history [count]`: 과거 입력 표시
- `:h? <string>`: 과거 입력 검색
- `:imports [name name ...]`: 과거 임포트 내역을 출력, 이름이 어디서 왔는지 식별
- `:implicits [-v]`: 현재 영역 안의 암시들을 보여줌
- `javap <path or class>`: 파일이나 클래스 역어셈블
- `line <number or line>`: 지정한 번호나 줄을 과거 입력 목록의 맨 뒤로 보낸다
- `load <path>`: 경로로 주어진 파일의 내용을 실행
- `:paste [-raw] [path]`: 붙여넣기 모드로 들어가거나, 경로에 있는 파일을 붙여넣음
- `:power`: 고급 사용자 모드 진입
  - 추상구문 트리나 인터프리터 프로퍼티 등 메모리상 데이터를 살펴보거나 컴파일러를 조작할 수 있는 추가 명령 제공
- `:quit`: 인터프리터 종료
- `:replay`: 실행을 재설정, 모든 예전 명령 재실행
- `:reset`: REPL을 초기 상태로 재설정하고, 모든 세션 엔트리 삭제
- `:save <path>`: 세션을 나중에 실행할 수 있도록 파일에 저장
- `:sh 명령 인자`: shell 명령을 실행 (결과는 암시적으로 `List[String]`)
- `:settings [+ or -]<option>` 컴파일러 옵션 플래그를 사용 가능하게 만들거나(+) 불가능하게 만든다(-)
- `:silent`: 결과를 자동으로 출력하게 하거나 출력하지 않게 한다
- `:type [-v] 식`: 식을 계산하지 않고 타입만 표시
- `:kind [-v] 식`: 식의 타입이 속하는 계를 표시
- `:warnings`: 경고가 발생한 행 중 가장 최근에 발생한 경고 표시

##### 스크립트 작성
- unix
```
#!/bin/sh
# src/main/scala/toolslibs/secho
exec scala "$0" "$@"
!#
print("You entered: ")
args.toList foreach { s => printf("%s", s) }
println
```

```shell
$ secho Hello World
You entered: Hello World
```

- windows
```bat
::#!
@echo off
call scala %0 %*
goto :eof
::!#
print("You entered: ")
args.toList foreach { s => printf("%s", s) }
println
```

#### scalac에 대비한 scala의 한계
##### scala로 실행한 스크립트 파일은 내용일 익명의 `object`에 둘러싼 것과 비슷
  - `object`에는 패키지 선언이 불가 => 스크립트 내부에서는 역시 패키지 선언 불가
  ```scala
  object Script {
    def main(args: Array[String]): Unit = {
      new AnyRef {
        // script code here
      }
    }
  }
  ```
##### 올바른 스크립트인데 `-Xscript <object>` 옵션을 사용하지 않으면 scalac로 컴파일할 수 없는 것도 존재
  - `object`는 컴파일할 객체의 이름, 앞의 예제에 있는 `Script`를 대신함
  - 컴파일러 옵션은 `REPL`이 암시적으로 만들어주는 것과 같은 래퍼 생성

##### 타입의 밖에 함수 정의나 함수 호출이 위치할 수 없기 때문에 컴파일을 하려면 래퍼 객체가 필요
- [example.sc](../src/main/scala/toolslibs/example.sc) 컴파일시 발생 에러내용
```bash
$ scalac MessagePrinter src/main/scala/toolslibs/example.sc
example.sc:3: error: expected class or object definition
def printMessage(msg: Message) = println(msg)
^
example.sc:5: error: expected class or object definition
printlnMessage(new Message("This works fine with the REPL"))
^
two errors found
```

##### `-Xscript` 사용
```bash
$ scalac -Xscript MessagePrinter src/main/scala/toolslibs/example.sc
$ scala -classpath . MessagePrinter
```
- 이 스크립트는 기본 패키지를 사용하기 때문에, 만들어진 클래스 파일은 현재 디렉토리에 포함

##### `javap -private`를 사용하여 각 파일을 살펴보면 내부에 들어있는 선언을 확인 가능
- `-p` 또는 `-private` 플래그는 비공개나 보호 멤버를 포함하는 모든 멤버를 표시하라는 의미
- 모든 옵션을 보고 싶다면 `javap -help` 사용
- `.class`를 빼고 `javap MessagePrinter$$anon$1$Message$`처럼 호출
```
MessagePrinter$$anon$1$Message$.class # Message 동반 객체
MessagePrinter$$anon$1$Message.class # Message 클래스
MessagePrinter$$anon$1.class # 전체 스크립트를 감싸도록 생성된 자바 클래스, 스크립트의 printMessage 메서드의 이 클래스의 비공개 메서드
MessagePrinter$.class # 이와 아래 MessagePrinter는 스크립트를 어플리케이션으로 만들면서 진입점을 제공하기 위해 생성한 래퍼
MessagePrinter.class # static main이 포함
```

### 21.1.3 scalap와 javap 명령행 도구
#### 디컴파일러
- 스칼라 구성 요소들이 JVM 바이트 코드로 어떻게 구현되는지 파악 가능
- 스칼라의 이름이 JVM에서 유효한 이름으로 어떻게 **변환** 되는지 파악 가능
- `javap`, `scalap` 모두 지원하는 실행 옵션을 설명해주는 `-help` 명령을 지원

##### `javap`
- JDK 제공 도구
- 바이트 코드를 분석해서 원래의 자바 소스코드에 들어있는 선언들을 출력
- `scalac`로 컴파일한 스칼라 코드까지 처리 가능
- `scalap -cp . MessagePrinter`
```scala
object MessagePrinter extends scala.AnyRef {
  def this() = { /* 컴파일한 코드 */ }
  def main(args: scala.Array[scala.Predef.String]): scala.Unit = {
    /* 컴파일한 코드 */
  }
}
```

##### `scalap`
- 스칼라 배포판에 포함
- 클래스 파일의 원래 스칼라 소스 코드가 어떤 모습인지 파악
- `javap -cp . MessagePrinter`
```java
Compiled from "example.sc"
public final class MessagePrinter {
  public static void main(java.lang.String[]);
}
```

##### 예제
- 복소수 구현 클래스: [Complex.scala](../src/main/scala/toolslibs/Complex.scala)
- 스칼라 2.11에서 만들어진 클래스 디컴파일: `javap -cp target/scala-2.11/classes toolslibs.Complex`
- `+`와 `-` 메서드의 이름이 어떻게 변환?
- `real`과 `imaginary` 필드에 대한 *Getter* 메서드의 이름? 타입?
- `scalap` vs `javap` 출력 타입?

### 21.1.4 scaladoc 명령행 도구
- `scaladoc` 명령은 `javadoc`에 해당: 스칼라 소스 파일로부터 문서를 생성
- `@author`, `@param` 등 `@` annotation 지원
- SBT에서 `doc`작업을 직접 실행 가능

### 21.1.5 fsc 명령행 도구
- **고속 스칼라 컴파일러**(fast scala compiler)
- 컴파일러를 더 빨리 시작할 수 있게 해 주는 *데몬 프로세스*
- 처음 실행되는 준비 작업에 대한 부가비용 제거
- 스크립트 반복 실행시 `fsc`유용 (버그 재현을 위해 `test suite` 반복 실행 등)


## 21.2 빌드 도구
- 대부분의 새로운 프로젝트는 [SBT](http://www.scala-sbt.org)를 빌드 도구로 사용
- [Ant](http://ant.apache.org), [Maven](http://maven.apache.org)(mvn), [Gradle](http://www.gradle.org) 등의 빌드 도구를 위한 스칼라 플러그도 있음


### 21.2.1 스칼라 표준 빌드 도구 SBT
- 스칼라와 자바 프로젝트를 빌드하기 위한 복잡한 도구(수많은 설정 옵션과 플러그인 기능 제공)
- 자세한 내용: http://bit.ly/13p0B4r

> **TIP**
SBT를 시작하는 **가장 빠른** 방법: 기존 빌드를 복사하여 변경하는 것

- 컴파일, 자동 테스팅 등 필요한 대부분의 작업과 각 작업간 적절한 의존관계 제공 (Maven과 유사)
- 프로젝트 이름이나 배포 버전등의 메타데이터를 지정
- 메이븐의 관례와 저장소를 사용하여 의존관계 지정
  - 의존관계 해결에는 [아이비](http://ant.apache.org/ivy)(ivy) 사용
- 다른 커스텀화를 수행하기 위해 SBT의 빌드 파일을 사용
- SBT는 스칼라 기반의 DSL을 언어로 사용

##### 둘 이상의 빌드 파일
- 복잡도나 프로젝트의 정교함, 커스텀화등을 고려하여 구성
-  주 빌드 파일: `build.sbt`
    - 루트 디렉터리나 `project` 디렉터리에 위치하는 것도 일반적
- 추가 빌드 파일
  - `build.properties`: 우리가 사용할 SBT의 버전 정의
  - `plugins.sbt`: 이클립스 프로젝트 파일을 생헝사기 위한 SBT 플러그인을 추가
  - 보통 `project` 디렉터리 밑에 위치

#### 빌드파일 예제
- `name := programming-scala, ...`와 같은 변수를 정의
- 현재의 DSL에서는 정의의 끝을 쉽게 추론할 수 있게 하기 위해 각 정의 사이에 빈 줄 삽입 필요
- SBT는 표준적인 저장소의 위치를 이미 알고 있지만 원하는 저장소 정의도 가능
```scala
name := "Programmin Scala, Second Edition: Code examples"

version := "2.0"

organization := "org.programming-scala"

scalaVersion := "2.11.2"

// 의존관계 정의
libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"   % "2.3.4",
  "org.scalatest"       %% "scalatest"    % "2.2.1"   % "test",
  "org.scalacheck"      %% "scalacheck"   % "1.11.5"  % "test",
  ...
)

// scalac 에 대한 컴파일러 플래그 정의
scalaOptions = Seq(
  "-encoding", "UTF-8", "-optimise",
  "-deprecation", "-unchecked", "-feature", "-Xlint", "-Ywarn-infer-any"
)

// javac 에 대한 컴파일러 플래그 정의
javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
```

```
initialCommands in console := """
  |import foo.bar._
  |import foo.bar.baz._
  |""".stripMargin
```
- REPL이 시작될 때 여러 명령을 자동으로 시작할 수 있도록 정의
- scala 옵션의 `-i <file>`과 비슷함
- 비슷한 두 가지...
  - `consoleQuick`: 컴파일 없이 REPL 시작, 컴파일 할 수 없거나 오래 걸리는 경우 무언가를 REPL에서 시험시 유용
  - `consoleProject`: 개발자의 코드는 무시하고, SBT와 CLASSPATH에 있는 빌드 정의 로드, 다른 유용한 임포트도 함께 수행

```
initialCommands in console := """println("Hello from console")"""
initialCommands in consoleQuick := """println("Hello from consoleQuick")"""
initialCommands in consoleProject := """println("Hello from consoleProject")"""
```
- `initialCommands in console`은 `consoleQuick`에 적용되나 `consoleProject`에는 적용되지 않음
- `consoleQuick`, `consoleProject`모두 별도로 값을 지정 가능
- 각각에 대한 `cleanupCommands`도 존재
  - 데이터베이스 세션 종료 등 자원 정리 코드 실행시 유용


### 21.2.2 다른 빌드 도구
- SBT에서 사용할 수 있는 것과 같은 점진적 컴파일 가능
  - 빌드 도구와 무관하게 걸리는 시간은 비슷해야 함

##### Ant
- `lib/scala-compiler.jar`: `scalac`, `fsc`, `scaladoc`등의 앤트 작업 내포
- 각각 대응하는 앤트 작업과 유사
- [스칼라 앤트 작업페이지](http://bit.ly/1wQotKr)에 있는 것과 같은 `build.xml` 설정 필요

##### Scala Maven
- [Github](http://bit.ly/1E8x6AW)
- 사전에 스칼라 설치 불필요(플러그인이 스칼라를 자동으로 다운로드)
- Intellij / Eclipse에 통합된 메이븐 지원 활용 가능

##### Gradle
- [Gradle document](http://bit.ly/1q8GQ5O)

## 21.3 IDE나 텍스트 편집기와 통합하기
- java IDE에 비해 아직 빈약함

#### 이클립스
- 플러그인: http://scala-ide.org
- 스칼라 플러그인 설치 혹은 플러그인이 이미 설정된 이클립스를 다운로드 가능
- 이클립스에서 스칼라 빌드시 Maven 사용: http://bit.ly/1xI13WQ
- 스칼라 프로젝트 파일 생성, SBT 빌드 & 테스트, 코드 네비게이션, 리팩토링 등 지원
- **워크시트**: REPL + 텍스트 편집기

#### Intellij
- Plugins preference > 스칼라 플러그인 검색 & 설치
- 이클립스에서 설명한 기능 대부분 제공

#### Net Beans
- **대화식 콘솔** 기능을 제공하는 플러그인 존재
- 소스포지(http://bit.ly/1tooTVZ) 참조

### 21.3.1 텍스트 편집기
- [Emacs](http://www.gnu.org/software/emacs), [Vim](http://www.vim.org), [Sublime Text](http://www.sublimetext.com)
- 각 에디터별로, 스칼라를 위한 플러그인 & 설정 옵션 제공
- [엔자임](http://github.com/ensime)(Ensime): 이맥스에 네비게이션이나 리팩토링 등의 'IDE 비슷한' 기능 지원


## 21.4 스칼라로 테스트 주도 개발하기
#### 테스트 주도 개발
- 코드 설계의 방향을 테스트를 통해 조정하는 잘 확립된 소프트웨어 개발 기법
- 기능이 작은 테스트를 **먼저** 작성 후, 테스트를 통과하는 코드를 **그 다음에** 작성
- 객체지향 개발자 사이에서 유명
- 함수형 프로그래머들은 REPL을 사용해 타입과 알고리즘 테스트 후 코드를 작성하는 경향이 있음
  - 이런 방법은 TDD에서 만들어지는 *영구적이고 자동화된* **검증** 스위트 작성 불가
  - 하지만 함수형 코드가 **순수** 한 경우, 시간이 지나도 깨지는 경향이 더 적다
  - 코드를 작성 후 **역행 테스트(regression test) 스위트를 제공하기 위해 테스트를 작성**

#### 테스트 라이브러리
- [ScalaTest](http://scalatest.org), [Spec2](http://bit.ly/1vovYm2)
- 다양한 스타일의 테스트를 작성 가능한 **DSL** 제공
- ScalaTest는 여러 트레이트를 혼합해서 다양한 스타일을 선택할 수 있도록 지원

#### 특성 기반 테스트(property-based test), 타입 기반 테스트(type-based test)
- 스칼라와 같은 **풍부한 타입 시스템을 제공하는 함수형 언어** 는 타입을 지정하는 것도 매번 컴파일을 실행할 때 마다 일종의 역행 테스트를 수행하는 셈
- 잘못된 상태를 가능한 한 없애주는 타입을 정의: 이런 타입도 특성을 잘 정의해야 함
- 하스켈의 [퀵체크](http://bit.ly/1E8x9Na)(QuickCheck)를 통해 유명해 진 후 기타 여러 언어에 이식
- 특정 타입의 모든 인스턴스에 대해 항상 참이어야 한다는 조건 명시(16.1 대수적 데이터 타입 참조)
- 특성 기반 테스트 도구: 이런 조건을 자동으로 생성한 대표적인 인스턴스의 샘플을 사용하여 검증
- 전통적인 TDD 도구: 모든 가능성을 시도해서 대표적인 예제를 생성해내는 것은 테스트를 작성하는 사람의 책임
- [ScalaCheck](http://scalacheck.org): 하스켈의 퀵체크를 스칼라에 이식
  - ScalaTest, Spec2에는 스칼라체크의 특성 테스트를 실행 가능
  - [JUnit](http://junit.org)나 [TestNG](http://testng.org)와 함께 사용: 자바와 스칼라 테스트를 쉽게 혼용 가능

> **TIP**
자바 코드베이스에서 스칼라를 최소한의 위헙으로 사용해 보고 싶다면, 자바 코드 테스트를 위해 스칼라 테스트 도구를 고려해 볼 것
제한적으로 스칼라를 사용해 볼 수 있지만 여러 위험을 줄일 수 있음

- SBT, Ant, Gradle은 세 가지 도구를 모두 지원

> **TIP**
세 도구 모두 스칼라의 **내부** DSL의 좋은 예
이들을 공부하면 직접 DSL을 작성할 때 도움이 될 수 있음


## 21.5 서드파티 라이브러리
- 지속적으로 변화. (언어도 마찬가지 아닌가?)
- [TypeLevel.scala](http://typelevel.org)

### Full Stack Library
- Web Frontend(HTML, Template enging, Javascript, CSS) + BackEnd

#### [Play](http://www.playframework.com/)
- 타입세이프 지원.
- 스칼라, 자바 API를 제공
- AKKA와 통합

#### [Lift](http://liftweb.com)
- 스칼라 최초의 풀스택 프레임워크


### BackEnd Libraries

#### [Akka](http://akka.io)
- 액터 기반의 종합적인 분산 계산 시스템
- [17.3 액터를 활용한 튼튼하고 확장성 있는 동시성 프로그래밍](https://github.com/masunghoon/study_programming-scala/blob/master/docs/17_Tools%20for%20Concurrency.md#173-액터를-활용한-튼튼하고-확장성-있는-동시성-프로그래밍) 참조

#### [Finagle](http://bit.ly/1vowyQO)
- JVM 기반의 서비스를 함수형 추상화를 기반으로 재생성
- 트위터에서 트위터 서비스를 구성하기 위해 사용중
- [Functional Systems(paper)](http://bit.ly/13p0N3I): 피네이글과 그 설계 철학에 대한 최근 대담

#### [Unfiltered](http://bit.ly/1s0F5s3)
- HTTP 요청을 서비스
- 여러 뒷단 서비스 앞에 일관성 있는 API를 제공하기 위한 Toolkit

#### [Dispatch](http://bit.ly/1087FQQ)
- 비동기 HTTP를 위한 API


### 고급 라이브러리
- 타입 시스템의 특징을 탐구, 함수형 프로그래밍 구성 요소를 구현

#### [Scalaz](http://bit.ly/1ud2bC9)
- 스칼라에서 카테고리 이론 개념을 선구적으로 시도한 라이브러리
- 여러 설계 문제를 해결하기 위한 손쉬운 도구 제공
- 7.4.4 스칼라제드의 Validation, [16.2 카테고리 이론](https://github.com/masunghoon/study_programming-scala/blob/master/docs/16_Advanced%20Functional%20Programming.md#162-카테고리-이론) 참조


### I/O Libraries
- [scala.io](http://bit.ly/1s0Fef2) 패키지는 제한적인 I/O만 제공, JAVA I/O는 사용하기 쉽지 않음
- 다음 두 라이브러리가 위의 문제를 해결

#### [Scala I/O](http://bit.ly/1nWmZeh)
- 모든 기능을 제공

#### [Rapture I/O](http://rapture.io)
- java.io를 감싸서 더 좋은 API로 제공


### Etc
- 특정 설계 문제를 해결

#### [Scopt](http://github.com/scopt/scopt)
- 명령행 구문분석 라이브러리

#### [Typesafe Config](http://github.com/typesafehub/config)
- 설정 라이브러리(JAVA API)

#### [Scala ARM](http://bit.ly/13oZkdG)
- Joshua Suereth
- 자동 자원 관리 라이브러리

#### [Typesafe Activator](http://github.com/typesafehub/activator)
- 예제 스칼라 프로젝트 관리
- http://typesafe.com/activator 에서 서비스


### Big-Data & Mathmatics
- [18.5 스칼라 기반 데이터 도구 목록](https://github.com/masunghoon/study_programming-scala/blob/master/docs/18_Scala_for_Big_Data.md#185-스칼라-기반-데이터-도구-목록) 참조


### 스칼라 2.11 선택 모듈
- 라이브러리 모듈화 과정에서 덜 사용되는 라이브러리 구성요소를 선택 요소로 전환

#### XML
- `scala-xml`
- XML 구문분석기 생성

#### Parser
- `Combinators`
- 구문분석기를 만들기 위한 콤비네이터로 이루어진 스칼라 파서 콤비네이터 라이브러리

#### Swing
- `scala-swing`
- 스윙 라이브러리

#### Async
- `scala-async`
- 스칼라 비동기 프로그래밍을 위한 라이브러리
- 퓨처와 작업하기 위한 직접적인 API 제공

#### Partest, Partest Interface
- `scala-partest`, `scala-partest-interface`
- 스칼라 컴파일러와 라이브러리를 위한 테스트 프레임워크


### [Awesome Scala](http://github.com/lauris/awesome-scala)
- 스칼라 서드파티 라이브러리 총 망라
- 자매품: http://ls.implicit.ly

## 21.6 마치며
#### 배운 것
- 스칼라 도구의 종류 및 활용법

#### 배울 것
- 자바와 스칼라 코드가 어떻게 상호 운용되는가?
