# 24 메타 프로그래밍: 매크로와 리플렉션

## 24.1 타입을 이해하기 위한 도구

## 24.2 실행 시점 리플렉션
- 언어의 의미를 *비틀고* 컴파일 시점에는 알 수 없는 코드를 읽기 위함
- **극단적으로 늦게 바인딩하기**(extreme late binding)

#### 리플렉션을 사용해서...
- `CLASSPATH`에 있는 바이트코드 중 상응하는 타입을 찾아 인스턴스 생성
- IDE: 플러그인 찾아 적재(?), 자동완성, 타입검사 등에 활용
- 바이트 코드 도구(Byte-code tools)는 보안상 취약점 등의 문제 파악

### 24.2.1 타입에 대한 리플렉션
[reflect.sc](../src/main/scala/metaprogramming/reflect.sc)
- `AnyRef`의 서브타입에서만 사용 가능
- `getFields`는 스칼라 타입의 `C`에 있는 필드를 인식 불가

[reflect2.sc](../src/main/scala/metaprogramming/reflect2.sc)
- `isInstanceOf`: 어떤 객체가 타입에 일치하는지 검사
- `asInstanceOf`: 객체를 다른 타입으로 변환
- 이런 메서드 대신 **패턴 매칭** 을 권장

### 24.2.2 클래스 태그, 타입 태그, 매니페스트
- 핵심 라이브러리에는 작은 리플렉션 API만 포함, 고급 리플렉션은 별도 라이브러리

#### `ClassTag`
- JVM이 **타입 소거** 에 의해 제거될 수 있는 정보를 일부 보관하기 위한 도구

[mkArray.sc](../src/main/scala/metaprogramming/mkArray.sc)
- 컴파일러는 자신이 아는 타입 정보를 사용해 암시적인 `ClassTag` 생성하지만, 이미 만들어진 리스트 제공시 타입 정보가 소거된 후
- 컬렉션을 생성과 그에 상응하는 `ClassTag` 생성을 동일한 영역에서 진행하고, 한 번에 전달할 필요가 있다
- `ClassTag`는 바이트 코드에서 타입 정보를 부활 시킬 수 없다. 하지만, 타입 정보를 소거 전에 확보하기 위해 `ClassTag`사용
- `scala.reflect.api.TypeTags#TypeTag`가 약화된 버전.
  - `TypeTag`는 전체 컴파일러 정보를 보존
  - `ClassTag`는 오직 런타임 정보만 반환 
  - 추상 타입을 위한 `scala.reflect.api.TypeTags#WeakTypeTag`도 있다
- ~~`Manifest`~~: deprecated
