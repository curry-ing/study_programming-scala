# 19 스칼라 동적 호출
#### 정적 타입 시스템
- 런타임에 정확성 보장
- 코드의 가독성 증가시키는 안전한 제약조건 부가
- 대규모 시스템 등에서 장점이 많음

#### 동적 타입 시스템
- 컴파일 타임에 존재하지 않던 메서드를 런타임에 호출하는 경우?
- `ActiveRecord` from **Ruby on Rails**

## 19.1 동기를 불어넣는 예제: 루비 온 레일즈의 `ActiveRecord`
- **ROR** 의 ORM(Object-Relational Mapping) 라이브러리
- **DSL 제공**:도메인 객체에 대한 메서드 호출을 연쇄시킴으로써 질의 조합 가능
- `method_missing`
  - 루비가 정의되지 않은 메서드 호출을 처리할 때 사용하는 메서드
  - 보통 예외를 발생시키지만, 일부 클래스에서는 다른 작업을 하도록 오버라이딩 가능
  - **ActiveRecord** 는 이런 기능을 활용해 SQL질의를 구성하기 위한 명령으로 사용

#### 예제
##### 테이블 스키마 정의
```sql
CREATE TABLE states (
  name        TEXT,
  capital     TEXT,
  statehood   INTEGER
);
```

##### `ActiveRecord`를 사용한 질의 구성
```ruby
# 이름이 `Alaska`인 모든 주를 찾는다
State.find_by_name("Alaska")
# 이름이 `Alaska`이면서 1959년에 미국에 가입한 모든 주를 찾는다
State.find_by_name_and_statehood("Alaska", 1959)
```
- 컬럼이 많은 테이블인 경우 모든 가능한 컬럼 조합에 대하 `find_by_*`메서드 구현은 쉽지 않다
- 명명 규칙에 따라 정한 **절차** 가 있다면 룰에 따른 자동화를 통해 해결 가능

#### `ActiveRecord`
- 메서드의 이름을 구문분석 후,
- 그에 상응하는 SQL 질의를 구성하고,
- 결과를 저장하기 위한 메모리상의 객체를 만들어내기 위한 모든 준비 코드를 자동으로 생성
- 일종의 **내장** DSL을 구현
  - DSL: 자체적인 문법이나 구문 구조를 가지지 않고, 호스트 언어인 루비의 용법을 따르는 언어


## 19.2 Dynamic 트레이트를 사용하여 스칼라에서 동적 호출하기
#### [`scala.Dynamic`](http://bit.ly/1pbhB8b)
  - 메서드의 동적인 호출을 가능케 하는 트레이트
  - 내부에 정의된 메서드가 없음
  - 컴파일러가 이 트레이트를 보면 미리 약속된 처리 절차를 따름

##### 예제
```scala
foo.method("blah")      ~~> foo.applyDynamic("method")("blah")
foo.method(x = "blah")  ~~> foo.applyDynamicNamed("method")(("x", "blah"))
foo.method(x = 1, 2)    ~~> foo.applyDynamicNamed("method")(("x", 1), ("", 2))
foo.field               ~~> foo.selectDynamic("field")
foo.varia = 10          ~~> foo.updateDynamic("varia")(10)
foo.arr(10) = 13        ~~> foo.selectDynamic("arr").update(10, 13)
foo.arr(10)             ~~> foo.applyDynamic("arr")(10)
```

- `Foo`는 호출될 가능성이 있는 *`Dynamic`* 메서드를 정의
- `applyDynamic`: 이름이 붙은 매개변수를 사용하지 않는 메서드 호출에 사용
- `applyDynamicNamed`: 사용자가 매개변수 중 어느 하나에라도 이름을 지정할 경우 호출
- 첫 번째 인자 목록: 호출된 메서드 이름
- 두 번째 인자 목록: 실제로 전달된 인자(가변 길이, 정해진 개수와 타입의 모두 허용 가능)
- `selectDynamic` & `updateDynamic`: 배열이 아닌 필드를 읽고 쓰기 위한 것
- `selectDynamic().update()`: 배열 원소를 쓰기 위한 특별한 형태
- 배열 원소를 읽는 경우, 그 호출을 단일 인자에 대한 메서드 호출과 구분 불가능: `applyDynamic`사용 필요

[clinq-example.sc](../src/main/scala/dynamic/clinq-example.sc)

[CLINQ.scala](../src/main/scala/dynamic/CLINQ.scala)


## 19.3 DSL에서 고려할 점
1. 구현에 대한 이해가 쉽지 않음 -> 유지보수, 디버깅, 확장이 어려움
2. 유의미한 오류 메시지 제공의 어려움  
3. 개발자가 논리적으로 옳지 못한 내용을 작성할 수 없도록 막는데에 대한 어려움

## 19.4 마치며
#### 배운 것
- **후크**(hook): 루비와 같은 동적 타입 언어와 같이 동적으로 메서드나 값을 정의하는 코드 생성 기능
- *후크* 를 이용한 **DSL** 구현 예제와 그 어려움

#### 배울 것
- 스칼라의 다양한 **DSL** 작성 도구
