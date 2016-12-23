# 15 스칼라 타입 시스템

## 15.1 경로에 의존하는 타입
**경로** 식을 사용해 내포시킨 타입에 접근 가능

```scala
class Service {
  class Logger {
    def log(message: String): Unit = println(s"log: $message")
  }
  val logger: Logger = new Logger
}

val s1 = new Service
val s2 = new Service{
  override val logger = s1.logger // overriding value logger in class Service of the `this.Logger`;
                                  // value logger has incompatible type.
}
```
- `Service`에 내포된 `Logger`
- `s1`과 `s2`는 각기 다른 인스턴스이고 그 내부에 `logger`역시 다른 타입으로 인식
  -> 타입은 **경로에 의존**(path-dependent) 적이다

### 15.1.1 `C.this`
```scala
class C1 {
  var x = "1"
  def setX1(x: String): Unit = this.x = x
  def setX2(x: String): Unit = C1.this.x = x
}
```
- 간단히 `this`라 표기할 수 있지만 이 `this`는 현재 인스턴스를 의미 -> `this` === `C1.this`

```scala
trait T1 {
  class C
  val c1: C = new C
  val c1: C = new this.C
}  
```
- `this` === `trait T1`

### 15.1.2 `C.super`
```scala
trait X {
  def setXX(x:String): Unit = {}
}

class C2 extends C1
class C3 extends C2 with X {
  def setX3(x: String): Unit = super.setX1(x)
  def setX4(x: String): Unit = C3.super.setX1(x)
  def setX5(x: String): Unit = C3.super[C2].setX1(x)
  def setX6(x: String): Unit = C3.super[X].setXX(x)
  // def setX7(x: String): Unit = C3.super[C1].setX1(x)    // error
  // def setX8(x: String): Unit = C3.super.super.setX1(x)  // error
}
```
- `C3.super` === `super` (`setX1`)
- 어떤 부모를 지칭하는지 `[T]`를 사용해 특정 가능(`setX5`:`C2`, `setX6`:`X` 선택)
- 특정하지 않는 경우, **선형화** 규칙에 따라 `super`의 대상을 결정(`setX2`)
- **조부모** 타입은 참조할 수 없고(`setX7`), `super`를 연속해 사용할 수 없다(`setX8`)

```scala
class C4 {
  class C5
}

class C6 extends C4 {
  val c5a: C5 = new C5
  val c5b: C5 = new super.C5
}
```
- 메서드 밖 타입 본문에서는 `super`를 사용해 부모타입 참조 가능

### 15.1.3 `(path).x`
- 내포된 타입에 접근하려면 마침표를 사용한 경로식 사용
- 경로의 마지막 부분은 안정적이지 않아도 무방(`Class`, `Trait`, `Type Member`등 사용 가능)
- 마지막분을 제외한 나머지 부분은 **안정적**(stable)이어야 한다 (`package`, `singleton Object`, 또는 그 둘에 대한 `type alias`)

```scala
package P1 {
  object O1 {
    object O2 {
      val name = "name"
    }
    class C1 {
      val name = "name"
    }
  }
}

class C7 {
  val name1 = P1.O1.O2.name
  type C1   = P1.O1.C1
  val c1    = new P1.O1.C1
  val name2 = P1.O1.C1.name
}
```
- `name1`, `C1`, `c1`은 경로의 마지막 부분을 제외하고는 모두 안정적 요소 사용
- `name2`에는 안정적이지 않은 요소 `C1`이 중간에 위치하여 컴파일 오류 발생


## 15.2 의존적 메서드 타입

#### 자석 패턴
- 처리를 위한 메서드가 하나 있어, **자석** 이라 불리는 객체를 넘겨받고, 그 객체(자석)는 호환 가능한 반환 타입을 보장 
-

