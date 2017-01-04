# 14 스칼라 타입 시스템
- 정적 타입 언어: 변수를 선언하면 그 변수의 타입이 고정되는 언어
- 타입 시스템이 가장 복잡한 편: FP + OOP 개념이 엮여있어서…
- 타입 추론이 이 복잡함을 어느정도 커버해 준다

## 14.1 매개변수화한 타입
### 14.1.1 변성 표기
- 공변성: `List[+A]`로 표기, `List[String]`이 `List[AnyRef]`의 서브타입
- 반공변성: `List[-A]`로 표기, 공변성과 반대, `Function2[-T1, -T2, +R]`

### 14.1.2 타입 생성자
- 어떤 클래스의 인스턴스를 만들어 낼 때 **인스턴스 생성자** 를 사용하는 것처럼 **구체적인 타입을 만들어낼 때 매개변수화한 타입을 사용한다** 는 사실 반영
- ex> `List`는 서로 다른 두 타입 `List[String]`과 `List[Int]`의 타입 생성자
- 타입 매개변수가 없는 클래스: 타입 매개변수 인자가 하나도 없는 **매개변수화한 타입**

### 14.1.3 타입 매개변수의 이름
##### 네이밍 규칙
1. 아주 일반적인 타입 매개변수에 대해서는 `A`, `B`, `T1`, `T2`같이 간략하게
2. 하부 컨테이너와 밀접한 연관이 있는 타입에 대해서는 좀 더 서술적으로

## 14.2 타입 바운드
### 14.2.1 상위 타입 바운드    
: 어떤 타입이 특정 다른 타입의 **서브타입** 이어야 한다는 제약
```scala
implicit def refArrayOps[T <: AnyRef](xs: Array[T]): ArrayOps[T] =
	new ArrayOps.ofRef[T](xs)
implicit def longArrayOps(xs: Array[Long]): ArrayOps[Long] =
	new ArrayOps.ofLong(xs)
... // 다른 AnyVal 타입에 대한 메서드
```

- `A <: AnyRef`: `AnyRef`의 서브타입인 어떤 타입 `A` (`A`는 자기 자신의 슈퍼타입일수도, 서브타입일 수도 있다)
  - `A`는 `AnyRef`일 수도 있다
  - `<:`: 왼쪽의 타입이 오른쪽으로부터 파생되었거나 (왼쪽이 서브클래스) 같아야 한다
- 첫 번째 메서드(`refArrayOps`)를 `AnyRef`의 서브타입에만 적용할 수 있도록 제약
  - 일반 변환 메서드와 `Long`, `Int`등의 구체적 변환 메서드간 모호성 제거

> **NOTE**  
> 타입 바운드와 변성 표기는 서로 관련이 없는 주제  
> **타입 바운드**: 매개변수화한 타입의 매개변수에 쓰일 수 있는 타입의 종류 제한(`T <: AnyRef`는 `T`를 `AnyRef`의 서브타입으로 제한)   
> **변성 표기**: 어떤 매개변수화한 타입의 서브타입의 인스턴스를 슈퍼타입의 인스턴스를 대용할수 있는지 지정(`List[+T]`에서 `List[String]`은 `List[Any]`의 서브타입  

### 14.2.2 하위 타입 바운드
: 어떤 타입이 다른 타입의 **슈퍼타입** 이어야 한다는 제약
```scala
sealed abstract class Option[+A] extends Product with Serializable {
	...
	@inline final def getOrElse[B >: A](default: => B): B = { ... }
	...
```

- `Option` 인스턴스가 `Some[A]`인 경우 그 안에 들어있는 값을 반환
  - 그렇지 않으면, `default`를 평가해서 반환
  - 이름에 의한 매개변수인 `A`는 슈퍼타입을 허용

#####  `val op3: Option[parent] = Option[Child](null)`
- `op3`는 `Option[Child](null)`(즉, `None`)을 `Option[Parent]`에 대입
- 호출하는 코드가 `Option[Parent]`로부터 `Parent`를 뽑아낼 것이라고 가정
- 실제로 `Option[Parent]`가 `None`인 경우에 기본값으로 인자에 지정한 `Parent`를 반환
- `Some[Parent]`이거나, `Some[Child]`인 경우에도 내부에 들어있는 값을 `Parent`타입으로 돌려받음(`Some`내부의 인스턴스가 `Child`의 서브클래스의 인스턴스일지라도 그렇다)

##### `getOrElse`에서 타입 바운드를 제거
```scala
case class Opt[+A](value:A = null) {
	def getOrElse(default: A) = if (value != null) value else default
}
```  

- `op3`가 참조하는 타입은 `Option[Child]`이기 때문에 이런 경우 `getOrElse`도 `Child`의 인스턴스를 요구하기 때문에 `op3.getOrElse(new Parent(20))`은 타입 검사를 통과하지 못함
- `default`가 해당 타입 매개변수의 슈퍼타입인 `B >: A`가 아닌 타입 매개변수의 인스턴스를 받아들이려는 시도: 공변성 관련 에러 발생
- 컴파일러가 단순한 시그니처가 아닌 `[B >: A]` 형태의 바운드를 필요로 하는 이유

##### 위 예제에서 공변적인 `Opt[+A]`를 무공변적인 `Opt[A]`로 변경
[lower-bounds2.sc](../src/main/scala/typesystem/bounds/lower-bounds2.sc)  

- 더 이상 `Opt[Child]`를 `Opt[Parent]` 참조에 대입할 수 없다
- 매개변수화한 타입이 타입 매개변수에 대해 **공변적** 인데, 메서드 중 일부에서는 **반공변적** 인 동작이 꼭 필요한 경우가 존재

#### `Seq.+:` 의 예제
```
scala> 1 +: Seq(2, 3)
res0: Seq[Int] = List(1, 2, 3)
```

#####  Signature
```scala
def +:(elem: A): Seq[A] = { … } 			// 1
def +:[B >: A, That](elem: B)(			// 2
  implicit bf: CanBuildFrom[Seq[A], B, That)]): That = { … }
```
- 실제 시그니쳐인 `2`는 임의의 새로운 슈퍼타입의 요소를 앞에 삽입 가능하며, CanBuildFrom을 사용
##### `0.1 +: List(1, 2, 3)`의 예제
- `B`는 원래 Double로 새 머리 값의 타입과 다름
	- 여기서 **최소 상위 바운드**(Least Upper Bound), 즉 원래의 타입 `A(Int)`와 새 원소의 타입 `Double`에 가장 가까운 슈퍼타입인 **`AnyVal`** 로 추론
	- 편리하지만, 더 범위가 넓은 LUB타입을 추론하는 것은 원래의 타입 매개변수를 변경하려는 의도가 없는 경우 의외일 수 있다(2.11부터는 컴파일 경고)
	- 반환 타입을 명시함으로서 모호성 해결: `val res: List[AnyVal] = 0.1 + List(1, 2, 3)`

### 정리
- 매개변수 타입에 대해 **공변적** 인 매개변수화한 타입과 메서드 인자의 **하위 타입 바운드** 사이에는 밀접한 관계가 있다
- 상위와 하위 타입 바운드간 상호 조합 가능

```scala
class Upper
class Middle1 extends Upper
class Middle2 extends Middle1
class Lower extends Middle2
case class C[A >: Lower <: Upper](a: A)
// case class C2[A <: Upper >: Lower](a: A) // compile error: 하위 바운드를 상위 바운드보다 먼저 표시해야 함
```

## 14.3 맥락 바운드
[implicitly-args.sc](src/main/scala/implicits/implicitly-args.sc)  

- `sortBy1`: 암시적 매개변수가 겉에 드러나 있다
- `sortBy2`: ‘감춰진’ 매개변수는 매개변수화한 타입
  - `[B: Ordering]`: B에는 아무 조작을 가하지 않고 `Ordering[B]`라는 타입의 암시적 매개변수로 넘기는 것과 같음 (적절한 `Ordering[B]`가 존재하지 않을 때 B라는 구체적인 타입을 사용할 수 없다는 뜻)

## 14.4 뷰 바운드
```scala
class C[A] {
	def m1[B](...)(implicit view: A => B): ReturnType = { ... }
  def m2[A <% B](...): ReturnType = { ... }
}
```
##### 뷰의 정의
타입 `A`를 타입 `B`의 한 가지로 바꿔주는 변환 함수가 있을 때 **`B`는 `A`에 대한 뷰 중 하나**  

- `A <: B`와 비교:
  - `A`가 `B`의 서브타입어야 하는 상위 바운드식
  - 뷰 바운드는 좀 더 느슨한 요구 사항
- 맥락 바운드에 비해 구문식이 더 깔끔하지만, 충분히 맥락 바운드로 구현할 수 있고, 맥락 바운드가 더 일반적이므로 그것을 사용할 것을 권장 (deprecate 가능성)

## 14.5 추상 타입 이해하기
- OOP의 매개변수화한 타입 & FP의 추상타입의 접근 방법은 일부 비슷한 부분이 있다
[abstract-types-ex.sc](../src/main/scala/typesystem/abstracttypes/abstract-types-ex.sc)

#### 추상 타입의 특징
- 타입 멤버에 대한 변성 표기는 불가능.
- 이런 타입은 타입을 둘러싸고있는 타입의 **멤버** 이지 매개변수 타입이 아니다
- 둘러싼 타입은 다른 타입과 상속 관계에 있을수 있지만, 멤버 타입은 다른 멤버 메서드나 변수와 똑같이 동작한다
- 멤버 타입은 둘러싼 타입의 상속 관계에 영향을 끼치지 못한다
- 다른 멤버와 마찬가지로 멤버 타입도 추상 타입 또는 구체적인 타입으로 선언 가능
- 변수나 메서드와 달리 **멤버 타입** 은 서브타입에서 완전히 정의하지 않고 세분화 가능
- 추상 타입에 구체적인 정의가 주어진 다음에야 실제 인스턴스를 생성 가능

#### 추상 타입 선언 예제
```scala
trait T1 { val name1: String }
trait T2 extends T1 { val name2: String }
case class C(name1: String, name2: String) extends T2
```

#### 추상 타입 멤버를 정의하는 구체적 타입을 선언하고 값을 초기화 하는 예제
```scala
object example extends exampleTrait {
	type t1 = T1
	type t2 = T2
	type t3 = C
	type t4 = Vector[T1]

	val v1 = new T1 { val name1 = "T1" }
	val v2 = new T2 { val name1 = "T1", val name2 = "T2" }
	val v3 = C("1", "2")
	val v4 = Vector(C("3", "4"))
}
```


### 14.5.1 추상 타입과 매개변수화한 타입의 비교
#### ex1> 서로간 연결이 별로 없는 컬렉션 등의 컨테이너 (표준 라이브러리의 `Some`)
##### 매개변수화한 타입 사용
```scala
case final class Some[+A](val value: A) { ... }
```

##### 추상 타입 사용
```scala
case final class Some(val value: ???) {
	type A
	...
}
```

- 인자 `value`의 타입으로 `A`는 스스로의 영역에서 보이지 않기 때문에 사용할 수 없고, `Any`를 쓰자니 타입 안정성 침해
- 타입 매개변수를 생성자의 인자에 사용해야 하는 경우: **매개변수화한 타입** 이 좋은 접근방법

#### ex2> 여러 타입이 밀접한 연관이 있는 타입(패밀리)
[`typelessdomore/abstract-types.sc`](../src/main/scala/typelessdomore/SubjectObserver.scalaX)

- `BulkReader`에서 타입 바운드가 없는 추상타입 `In`은 서브타입 `StringBulkReader`, `FileBulkReader`에서 그 추상 타입을 정의
- 사용자가 더 이상 타입 매개변수를 사용해서 타입을 지정하지 않지만, 타입 멤버 `In`과 그 멤버를 내포하는 클래스를 마음대로 제어 가능: 여러 구현에서 일관성이 유지된다

#### ex3> `Observer` 패턴 설계
[`SubjectObserver.scalaX`](../src/main/scala/typesystem/abstracttypes/SubjectObserver.scalaX)  

- `_.receiveUpdate(this)` compile error
	- 주체와 관찰자에 대해 바운드된 추상 타입 멤버를 사용함으로써 구체적인 타입을 그 두 추상 타입(특히 `S` )에 지정한 경우 `Observer.receiveUpdate(subject: S)`가 덜 유용한 부모 타입인 `Subject`가 아니라 주체가 가져야 할 정확한 타입이 되게 만드는 것
	- `receiveUpdate`에 전달되는 타입은 `Subject`일 뿐 더 구체적인 어떤 타입 `S`가 되지 못함
	- 다음 절 **자기 타입 표기** 를 통해 해결 해 보자

## 14.6 자기 타입 표기
### `this`
- 특정 메서드 내에서 자신의 인스턴스를 참조
- 어떤 영역 내에서 같은 이름의 대상이 여럿 참조되는 경우 스스로를 참조할 때 모호성 제거

### `self`
#### 목적
- `this`에 대해 예상하는 타입이 무엇인지 추가로 지정
- `this`에 대한 별명 지정

[`SubjectObserver.scala`](../src/main/scala/typesystem/selftype/SubjectObserver.scala)
1. `self: S`: `Subject`가 실제로 `Subject`를 혼합한 구체적 클래스인 **스스로의 서브타입 `S`** 의 인스턴스가 될 것이라고 가정

[`ButtonSubjectObserver.scala`](../src/main/scala/typesystem/selftype/ButtonSubjectObserver.scala)

[`selftype-cake-pattern.sc`](../src/main/scala/typesystem/selftype/selftype-cake-pattern.sc)  

- `App`트레이트는 추상 트레이트로 구성된 인프라의 여러 계층을 하나로 엮는다
- `App.run`은 엮인 각 계층 내의 `start*`메서드를 호출함으로써 계층을 시작한다(어떤 구체적 트레이트도 사용되지 않음)
-> 구체적인 애플리케이션은 여기 사용된 추상 트레이트에 대한 구체적인 구현을 혼합

##### 자기 타입 표기 `self: Persistence with Midtier with UI =>`
- 자기 타입 표기에 복잡한 타입 표기를 추가하는 경우 해당 트레이트나 추상 클래스의 서브 타입을 **반드시 혼합해야 한다** 는 의미
- 구체적 인스턴스인 `MyApp`은 `App`을 확장하면서 `App`의 `self`타입에서 지정한 의존성을 만족시켜주는 여러 트레이트를 혼합: **케이크 패턴**

##### vs `extends … with …`
- 상속 기반의 구현: `App`이 `Persistence`, `Midtier`, `UI`의 서브타입
- 자기타입 표기: 혼합을 통해 동작을 조합하는 것임을 명확히 보여준다

> **`TIP`**  
> **자기 타입 표기**: 믹스인 조합을 더 강조한다.    
> **상속**: 서브타입 관계를 암시   

### `this`에 대한 별명
[`this-alias.sc`](../src/main/scala/typesystem/selftype/this-alias.sc)

#### 자기 타입 지정이 없다면… `C1.talk`를 `C3.talk` 내부에서 호출 가능할까?
- 두 메서드의 이름이 같아서 후자가 전자를 가리기 때문에 호출 불가능
- `C3`가 `C1`의 직접 서브타입(`C2`)도 아니기에 `super.talk`도 사용 불가
-> 자기타입 표기는 **`this`를 일반화한** 참조로 생각

## 14.7 구조적 타입
- 동적 언어의 **duck typing** 에 대한 type safe한 버전: 타입의 구조(필드, 메서드 등)를 판단여 객체를 구분할 수 있게 되면 이름은 몰라도 된다
- 스칼라는 구조를 통한 타입 유추를 **컴파일 시점** 에 판단하는 비슷한 메커니즘을 지원

[`Observer.scala`](../src/main/scala/typesystem/structuraltypes/Observer.scala)  

- `Subject`의 상태 변경을 보고 싶은 타입은 반드시 `Observer`를 구현해야 했으나,   `receiverUpdate`메서드만 구현함으로써 해결

#### 단점
- 스칼라에서는 구조적 타입이 추상 타입이나 타입 매개변수를 참조할 수 없음
- 타입 이름으로는 메서드 구현 여부를 확인할 수 없기에, 리플렉션을 사용해 메서드가 있는지 확인해야 한다: 부가 비용 발생, `import` 구문 추가

#### 장점
- 두 존재 사이의 결합을 최소화(메서드 시그니처 등에 의해서만 결합)

[`SubjectFunc.sc`](../src/main/scala/typesystem/structuraltypes/SubjectFunc.sc)   

- 이름을 바탕으로 한 모든 연결 제거
- 리플렉션을 통한 호출 불필요: 인자 타입으로 `Any` 사용 대신 `State`를 사용 가능
-> 극단적인 예일 뿐, 일반적인 상황에서까지 구조적 타입 지정이 불필요하다는 것은 아님


## 14.8 복합 타입
여러 타입을 조합한 인스턴스의 경우 **복합 타입** 을 얻게 된다
```scala
trait T1
trait T2
class C
val c = new C with T1 with T2
```  
- 위 예제의 경우 `c`의 타입은 `C with T1 with T2`
- `c`가 세 타입 모두의 서브타입으로 간주됨


### 14.8.1 타입 세분화
- 특정 인터페이스를 구현하는 **익명 내부 클래스** 정의 시 메서드 구현과, 필요한 다른 멤버를 추가로 정의하는 개념과 연관

#### java에서…
```java
List<C> listOfC = ...
java.util.Collections.sort(listOfC, new Comparator<C>() {
	public int compare(C c1, C c2) { ... }
	public boolean equals(Object obj) { ... }
});
```  
- 위 예제에서 기반 타입인 `Comparator`를 세분화하여 새로운 타입 생성
- JVM은 바이트코드에서 이 타입에 대한 유일한 이름을 생성하여 할당

#### Scala에서는…
```scala
val subject = new Subject {
	type State = Int
	protected var count = 0
  def increment(): Unit = {
	  count += 1
    notifyObservers(count)
  }
}
// subject: typesystem.structuraltypes.Subject{type State = Int; def increment(): Unit} = typesystem.compoundtypes.A$A14$A$A14$$anon$2@5fa6d905
```
[`compound-types.sc`](../src/main/scala/typesystem/compoundtypes/compound-types.sc)

- 반환되는 타입 시그니처에도 구조적으로 추가된 부분 추가
- 비슷하게, 인스턴스 생성 시 트레이트를 혼합하면 세분화한 타입 생성 <???>
- Reflection API: 세분화된 타입에 추가된 멤버를 인스턴스의 외부에서 접근시 사용

## 14.9 존재 타입
- 타입의 이름을 정확히 지정하지 않으면서도 해당 타입이 **존재** 한다고 선언 가능
- 타입 이름을 모르고, 현재 문맥에서는 그에 대해 알 필요도 없을 때 사용
- 자바와의 연동에 중요 (제네릭스를 지원하면서 타입 시스템의 정확성 유지)

[`Doubler.scala`](../src/main/scala/typesystem/existentials/Doubler.scala)  

- 같은 이름을 가진 두 메서드 인자가 원소 타입이 다른 List와 같은 컬렉션인 경우 타입 소거로 인해 오버로딩을 하지 못함
- 리스트의 각 원소를 개별적으로 검사하는 방식으로도 해결 가능
- `Seq[_]`는 실제 `Seq[T] forSome { type T }`라는 **존재 타입** 을 축약

| 짧은 표기 | 긴 표기 | 설명 |
| --- | --- | --- |
| `Seq[_]` | `Seq[T] forSome { type T }` | `T`는 `Any`의 서브타입 중 하나  |
| `Seq[_ <: A]` | `Seq[T] forSome { type T <: A }` | `T`는 다른 곳에 정의된 `A`의 서브타입 중 하나 |
| `Seq[_ >: Z <: A]` | `Seq[T] forSome { type T >: Z <: A }` | `T`는 `A`의 서브타입이면서 ,`Z`의 슈퍼타입인 타입 중 하나 |

- 스칼라 제네릭 구문인 `java.util.List[_ <: A]`는 자바의 변성 표현식인 `java.util.List<? extends A>`와 비슷
- 스칼라 변성 표기는 선언 지점에 정의되지만, 존재 타입 식을 사용하면 자바처럼 호출 지점의 변성 동작을 정의 가능(일반적이지는 않음)
- 타입을 더 이상 구체적으로 정의 불가능한 경우 `Seq[_]`와 같은 타입 시그니처를 사용(`forSome`을 사용하는 전체 존재 타입 구문은 볼 일이 별로 없다)

## 14.10 마무리
#### 배운 것
- 가장 흔히 접할 수 있는 타입 시스템의 기능
- 객체지향 상속의 미묘함 이해
- **변성** 이나 **타입 바운드** 와 같은 기능이 왜 중요한가

#### 배울 것
- 타입시스템 more advanced
