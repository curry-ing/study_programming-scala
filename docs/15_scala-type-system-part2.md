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
호환 가능한 반환 타입을 보장하는 **자석** 이라 불리는 객체를 넘겨받아 처리하는 메서드를 만드는 패턴

[dep-method.sc](../src/main/scala/typesystem/dependentmethodtypes/dep-method.sc)

- sealed trait `Computation`이 서비스가 수행할 모든 계산 유형(원격 & 지역)을 표현
- 수행할 작업은 **`Future`** 로 감싸여 있음 -> 비동기 실행
- `Await._`: `Future`가 완료될 때 까지 대기, 입력에 따라 `Local`, `Remote`중 하나 반환
- `handle`은 공통의 수퍼클래스를 반환하지 않고, 인자에 의존하는 타입을 반환


## 15.3 타입 투영

[type-projection.scala](../src/main/scala/typesystem/valuetypes/type-projection.scala)
[type-projection.sc](../src/main/scala/typesystem/valuetypes/type-projection.sc)

- `Object.Method` 처럼 사용을 위해서는 `Object`가 객체로 존재해야 함(클래스로 선언만 된 상태에서는 접근 불가능)
- 타입만 사용하고 싶은 경우: `#`를 사용하여 **투영**(project)
- 구체적으로 정의되지 않은 타입(추상 타입)의 경우 **투영** 이 불가능하다

#### 타입 지정자
일상적으로 사용하는 타입 명세: 실제로 타입 투영을 짧게 쓴 것

```scala
Int               // scala.type#Int
scala.Int         // scala.type#Int
package pkg {
  class MyClass {
    type t        // pkg.MyClass.type#t
  }
}
```

### 15.3.1 싱글턴 타입
- **싱글턴 객체** 와는 다르다 **싱글턴 객체** 와는... (싱글턴 객체: `object`로 선언)
- `AnyRef`의 서브타입인 인스턴스 `v`에는 가각 고유의 **싱글턴 타입** 이 존재(null 포함)
- `v.type`을 사용해 해당 타입 사용(타입 지정을 `v`가 지정하는 오직 **한** 인스턴스에만 한정함)

[type-types.sc](../src/main/scala/typesystem/valuetypes/type-types.sc)

- **싱글턴 객체** 는 인스턴스와 그와 대응하는 타입을 동시에 정의
[object-types.sc](../src/main/scala/typesystem/valuetypes/object-types.sc)



## 15.4 값에 대한 타입
#### 값 타입
- 모든 값에는 타입이 있고, 이런 타입이 취할 수 있는 모든 형태
- 종류: 매개변수화한 타입, 싱글턴 타입, 타입 투영, 타입 지정자, 복합 타입, 존재 타입, **튜플 타입**, **함수 타입**, **중위 타입** 등

### 15.4.1 튜플 타입
```scala
val t1: Tuple3[String, Int, Double] = ("one", 2, 3.14)
val t2: (String, Int, Double)       = ("one", 2, 3.14)
```
- `TupleN`의 선언자가 생략 가능하고, 각 괄호(`[]`)의 중복을 줄여 더 복잡한 타입을 간편하고 명료하게 사용

### 15.4.2 함수 타입
```scala
val f1: Function[Int, Double, String] = (i, d) => s"int $i, double $d"
val f2: (Int, Double) => String       = (i, d) => s"int $i, double $d"
```
- 함수의 타입은 화살표 표기(`->`)로 축약 가능

### 15.4.3 중위 타입
```scala
val left1: Either[String,Int]   = Left("hello")
val left2: String Either Int    = Left("hello")
val right1: Either[String,Int]  = Right(1)
val right2: String Either Int   = Right(2)
```
- 타입 매개변수가 두 개인 매개변수화한 타입은 중위 표기법으로 표현 가능
- 내포 가능: 항과 동일하게 타입 이름이 콜론(`:`)으로 끝나는 타입은 오른쪽으로 결합
  - `항`(term): 타입이 아닌 모든 식
  - 괄호를 사용하여 기본적인 결합 순서 변경 가능

[infix-types.sc](../src/main/scala/typesystem/valuetypes/infix-types.sc)


## 15.5 고계 타입

```scala
def sum(seq: Seq[Int]): Int = seq reduce (_ + _)
sum(Vector(1,2,3,4,5)) //
```

[Add.scala](../src/main/scala/typesystem/higherkinded/Add.scala)
[add-seq.sc](../src/main/scala/typesystem/higherkinded/add-seq.sc)

- `sumSeq`: 암시적인 `Add` 인스턴스 정의가 있는 모든 시퀀스의 합계 도출 가능
- `Seq`의 서브타입만 지원 -> 좀 더 일반적인 컬렉션을 지원하는 `sum`? : **고계 타입**


[Reduce.scala](../src/main/scala/typesystem/higherkinded/Reduce.scala)

- **고계 타입** 은 선택적 기능이기 때문에 따로 `import` 필요
- `M[T]`에 대해 반공변적: 무공변일 경우 `M[T]`의 `M`이 `Seq`인 암시적 인스턴스는 `Vector`와 같은 `Seq`의 서브타입에 대해 사용 불가(메서드의 인자는 **반공변적인** 위치에 있다)
- `seqReduce` & `optionReduce`: 메서드(값 아님), 타입 매개변수 `T`를 구체적 인스턴스에 맞춰 추론할 필요가 있다(암시적 `val` 사용불가)
- 타입 매개변수는 `Reduce`의 정의에서 추론됨: `SeqReduce[T] = new Reduce[T, Seq]{ ... }`에 `Seq` 타입 매개변수가 지정되지 않음

[add.sc](../src/main/scala/typesystem/higherkinded/add.sc)

- `T:Add`라는 맥락 바운드를 사용
- `M[T]`에 대해 `M[T]: Reduce`와 같은 맥락 바운드 적용?: `Reduce`가 타입 매개변수를 두 개 받고, 타입 바운드는 타입 매개변수가 하나만 존재하는 경우에 사용 가능하기 때문에 불가능
- 두 번째 인자에 암시적인 `Reduce` 매개변수를 받도록 설정


[Reduce1.scala](../src/main/scala/typesystem/higherkinded/Reduce1.scala)

- `Reduce`를 고계 타입 매개변수 하나만 받도록 오버라이딩 -> 맥락 바운드 타입 사용 가능
- `Reduce1` 추상화에는 타입 매개변수가 `M` 하나뿐.
- 고계타입 `M`은 여전히 반공변이나, 내부의 타입 매개변수는 지정하지 않음 -> **존재 타입**
- `T` 매개변수를 `reduce`메서드 쪽으로 이동

[add1.sc](../src/main/scala/typesystem/higherkinded/add1.sc)

- 맥락 바운드가 둘: `Reduce1`, `Add`
- `implicitly`에 주어진 타입 매개변수가 이 두 암시적 값 사이에 모호성 제거

> 고계 타입의 사용
고계 타입의 사용으로 인해 추상화는 한단계 더 발전할 수 있지만, 가독성은 떨어질 수 있다
`Scalaz`나 `Shapeless`와 같은 라이브러리는 고계 타입을 폭넓게 활용하여 간결학 광력한 코드 조합 가능
하지만, 추상화가 너무 심해서 배우고, 테스트하고, 디버깅하고, 발전시키기 어려운 코드를 만들지는 말자


## 15.6 타입 람다
- 타입 수준에서의 nested function 개념
- 맥락에 대해 너무 많은 타입 매개변수가 필요한 매개변수화한 타입이 필요한 상황에서 유리한 일종의 코딩 관용구

[Functor.scala](../src/main/scala/typesystem/typelambdas/Functor.scala)

- `Functor`라는 이름: 맵 연산을 제공하는 타입에 대한 일반적인 네이밍
- 컬렉션을 메서드의 인자로 넘기지 않고 `map2`라는 메서드를 제공하는 `Functor` 클래스로의 암시적 변환을 정의
  - `M[T]`가 반공변일 필요 없음(오히려 공변이 더 유리)
- `Functor`가 공변적이기에 `Seq`에 대한 암시적 변환을 `Seq`의 모든 서브타입에 사용 가능
- **타입 람다**(`type λ[α]`) 를 사용해 추가 타입 매개변수를 처리

#### 타입 람다 관용구의 확장 해석
```scala
... Functor[V1,             // 1
  (                         // 2
    {                       // 3
      type λ[α] = Map[K,α]  // 4
    }                       // 5
  )#λ                       // 6
]
```
1. `V1`은 타입 매개변수의 목록 시작, `Functor`의 두 번째 타입 매개변수는 컨테이너 타입이어야 하며, 그 컨테이너 타입 자체도 타입 매개변수를 하나 취한다
2. 두 번째 타입 매개변수 정의 시작
3. **구조적 타입** 정의 시작
4. `Map`에 대한 별명인 타입 멤버를 정의 (`λ`는 임의로 정한 이름이지만, 관용적이다)
  - 이 타입에는 자체 타입 매개변수인 `α`가 있음 -> `Map`의 값 타입을 지정하기 위해 `α` 사용
5. 구조적 타입 정의 끝
6. 2번 line의 끝이며 동시에 구조적 타입에 있는 `λ`에 대한 타입 투영 사용.
  - `λ`는 이후에 올 코드에서 추론될 내포된 타입 매개변수를 받는 `Map`에 대한 설명

- 타입 람다는 `Functor`가 지원하지 못하는 `Map`에 필요한 추가 타입 매개변수를 처리
- `α`는 이어지는 코드에서 추론
- `λ`나 `α`에 대해 명시적으로 다시 참조할 필요는 없다


## 15.7 자기 재귀 타입: `F-bounded` 다형성

- 자기 자신을 참조하는 타입

#### JAVA의 예> `Enum` 추상 클래스

```
public abstract class Enum<E extends Enum<E>>
extends Object
implement Comparable<E>, Serializable
```

- `Comparable<E>`의 `compareTo` 메서드 시그니처: `int compareTo(E obj)`
- 같은 타입에 정의된 열거값 중 하나가 아닌 객체를 `compareTo`에 넘기면 컴파일 오류 발생

#### Scala
- 재귀적인 타입을 사용하여 반환 타입이 호출한 타입과 동일한 메서드를 편하게 정의 가능

[f-bound.sc](../src/main/scala/typesystem/recursivetypes/f-bound.sc)

- `Parent`: 재귀적 타입, 위의 `Enum`과 같은 역할을 하는 구문
- `Child1(s: String) extends Parent[Child1]`: 파생 타입은 `X extends Parent[X]`라는 관용구를 따라야 한다


## 15.8 마치며
#### 배운 것
- 아직 스칼라를 효율적으로 사용할만큼 스칼라의 풍부한 타입 시스템의 복잡성을 이해할 수 있는건 아니다
- 복잡한 타입시스템을 사용하는 외부 라이브러리에 대한 이해가 높아지고, 그런 라이브러리를 만들 수 있겠다

#### 배울 것
- 함수형 프로그래밍의 고급 기법
