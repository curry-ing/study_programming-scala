# 18 스칼라를 활용한 빅데이터

#### 빅데이터에서의 FP
- 데이터 핸들링 함수인 `map`, `flatMap`, `filter`, `fold`등 콤비네이터의 자연스러운 사용
- **Java** 와의 생산성 측면에서 비교 우위
- 빅데이터 업계에서 - 개발자: **Scala** vs 데이터 과학자: **R** / **Python**


## 18.1 빅데이터: 간략한 역사

#### 세 가지 문제
1. 관계형 데이터베이스의 한계
2. 시스템 가용성
3. 대용량 데이터의 빠르고 효율적인 분석 필요성 증가


#### 아마존 다이나모
- 키-값 저장소 모델
- 트랜잭션 지원: 단일 Row에 한함
- 데이터는 [클러스터 전반에 샤딩](http://bit.ly/1yMfojJ)

- 수평으로 규모 확장 가능
- 읽기/쓰기 Throughput 증가
- 복제 전략 사용: 단일 로드나 랙이 실패하더라도 데이터 가용성 증가

#### 구글 파일시스템(GFS)
- 구글이 개발한 클러스터화 및 가상화된 파일시스템

#### 맵리듀스
- GFS위에 구축된 클러스터 상에 **작업** 을 분산시키고, 여러 노드에서 **작업** 을 수행할 수 있는 범용 계산엔진

#### 하둡
- GFS와 맵리듀스를 통합한 구현체
- HDFS라는 파일시스템 위에서 구동

#### 최근의 빅데이터 시스템
- **하둡** & **NoSQL DB** 의 혼합
- 빅데이터 도구: 유연성, 저렴한 가격으로 인한 다양한 형태의 데이터 저장 & 통합 & 분석 도구 (용량이 크지 않더라도...)
- 맵리듀스의 후계자와 그 중심에 있는 **Scala**


## 18.2 스칼라로 맵리듀스 계산하기
#### 맵리듀스 JAVA API
- 저수준 & 사용하기 어려움
- **맵(map)**: 파일을 읽어 데이터를 Key-Value 쌍으로 변환
- **리듀스**(reduce): K-V pair를 정렬 후 원하는 계산 수행
- 작업 간 디스크에 읽기 & 쓰기 연산의 반복
- 맵리듀스의 맵: **`flatMap`**, 리듀스: **`reduce`**
  - 이 두 가지 연산만으로 거의 모든 작업이 가능하지만 쉽지 않음

#### [캐스캐이딩](http://cascading.org)
- 하둡 맵리듀스를 기반의 자바 API
- 저수준의 내용을 추상화

#### [스캘딩](http://bit.ly/1wNnkzG)
- *캐스케이딩* 의 **Scala API**
- twitter에서 개발

#### 예제> 단어 세기
- 하둡계의 `Hello World!`
- 절차
  - **문서 읽기 맵**: 문서 뭉치를 병렬로 읽어 본문을 단어로 구분
  - **각각의 맵 작업**: 자신이 처리한 문서의 단어 빈도 계산 후 **(단어, 빈도)** 쌍의 시퀀스 반환
  - **리듀서** 는 동일한 단어의 쌍을 전달받아 병합계산 후 디스크에 기록

```java
class WordCountMapper extends MapReduceBase implements Mapper<IntWritable, Text, Text, IntWritable> {
  static final IntWritable one = new IntWritable(1);
  static final Text word = new Text;

  @Override public void map(IntWritable key, Text valueDocContents, OutputCollector<Text, IntWritable> output, Reporter reporter) {
    // 본문을 토큰으로 나누어 찾아낸 각 단어를 output의 키로 쓰고, 빈도를 값으로 씀
    String[] tokens = valueDocContents.toString.split("\\s+");
    for (String wordString: tokens) {
      if (wordString.length > 0) {
        word.set(wordString.toLowerCase);
        output.collect(word, one);
      }
    }
  }
}

class WordCountReduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
  public void reduce(Text keyWord, java.util.Iterator<IntWritable> counts, OutputCollector<Text, IntWritable> output, Reporter reporter) {
    int totalCount = 0;
    while (counts.hasNext) {
      // 각 단어 키에 대한 빈도수 컬렉션의 합계 계산.
      totalCount += counts.next.get;
    }
    output.collect(keyWord, new IntWritable(totalCount));
  }
}
```

- 침입성이 높고, 유연성이 적음(?, EJB 1.X API ?)
- too much boilerplates
  - 매번 직렬화 형식인 Writable로 감싸야 하는 등의 추가 작업
  - 자바 상용구
- 매우 단순한 알고리즘 구현에 60lines -> 복잡한 알고리즘은 복잡도가 현저히 증가
  - 2차 정렬 맵리듀스

##### 캐스캐이딩
- 수도꼭지 - 파이프  

```java
package impatient;

public class CascadingWordCount {
  public static void main (String[] args) {
    String input = args[0];
    String output = args[1];

    // 하둡 설정등을 위한 준비 코드
    Properties properties = new Properties();
    AppProps.setApplicationJarClass(properties, Main.class);
    HadoopFlowConnector flowConnector = new HadoopFlowConnector(properties);

    // HDFS를 위한 수도꼭지를 사용하여 데이터를 읽고 씀
    Tap docTap = new Hfs(new TextDelimited(true, "\t"), input);
    Tap wcTap = new Hfs(new TextDelimited(true, "\t"), output);

    // 레코드를 표현하는 튜플의 두 필드
    Fields token = new Fields("token");
    Fields text = new Fields("text");
    // 정규 표현식을 사용하여 텍스트를 토큰의 스트림으로 변경
    RegexSplitGenerator splitter = new RegexSplitGenerator(token, "[ \\[\\]\\(\\),.]");

    // 입력 텍스트를 반복하여 방문하며 단어만 토해내는 파이프 생성
    Pipe docPipe = new Each("token", text, splitter, Fields.RESULTS);
    Pipe wcPipe = new Pipe("wc", docPipe);

    // 그룹을 만드는 기준 키를 가지고 그룹 연산을 수행하는 새로운 파이프 생성
    wcPipe = new GroupBy(wcPipe, token);
    // 각 그룹을 세는 파이프 추가
    wcPipe = new Every(wcPipe, Fields.ALL, new Count(), Fileds.ALL);

    // 입력과 출력 수도꼭지를 배관에 연결하는 흐름을 생성
    FlowDef flowDef = FlowDef.flowDef()
      .setName("wc")
      .addSource(docPipe, docTap)
      .addTailSink(wcPipe, wcTap);

    // 만든 흐름을 실행
    Flow wcFlow = flowConnector.connect(flowDef);
    wcFlow.complete();
  }
}
```

- 임포트 제외 30여 lines
- API를 몰라도 로직 이해가 쉬움
  - 말뭉치를 단어로 나눔 -> 각 단어를 그룹화 -> 그룹의 크기 카운트
- SQL: `SELECT word, COUNT(*) FROM raw_words GROUP BY word;`

##### 스캘딩
- 스칼라의 익명함수, 고차함수 사용등으로 더 직관적인 코드

```scala
import com.twitter.scalding._

class WordCount(args: Args) extends Job(args) {
  TextLine(args("input")) // TextLine: HDFS, S3등의 로컬 파일시스템에 대한 추상화
                          // 스캘딩 작업을 실행한 방식에 따라 파일시스템 경로를 해석하는 방법이 달라짐 (캐스케이딩의 HadoopFlowConnector같은 부분)
    .read // 각 줄이 레코드인 텍스트 파일을 읽음
    .flatMap('line -> 'word) {  // 필드 이름을 지정하기 위해 스칼라의 심벌을 사용 (입력 필드: 'line, 출력필드: 'word)
      line: String => line.trim.toLowerCase.split("""\s+""")
    } // 각 line을 읽어 flatMap을 사용해 단어로 변경.
    .groupBy('word){ group => group.size('count) }  // 단어를 기준으로 그룹을 나누고 크기를 계산. 출력 스키마: ('word, 'count)
    .write(Tsv(args("output"))) // `--output 경로`로 지정한 위치에 탭으로 구분된 값(Tsv) 기록
}
```

## 18.3 맵리듀스를 넘어서
- 하둡에서의 실시간 처리
  - 맵 리듀스는 일괄 처리에 사용
  - HDFS는 증분 변경에 대한 지원 미비
  - Storm & Spark 등장

#### Spark
- 뱁리듀스를 대치
- 일괄처리 & 스트리밍 처리 모두 지원
- Scala로 구현
- 처리 성능이 뛰어남
- 간결하면서도 표현력이 뛰어난 직관적인 API채용
- RDD: Resilient Distributed Dataset(탄력성 있는 분산 데이터집합)
  - 클러스터에 분산 배치
  - 전체 처리에 대한 계보를 보유: 중단되더라도 재처리 가능

```scala
package bigdata

import org.apache.spark.SaprkContext
import org.apache.spark.SparkContext._

object SparkWordCount {
  def main(args: Array[String]) = {
    val sc = new SparkContext("local", "Word Count")
    val input = sc.textFile(args(0)).map(_.toLowerCase)
    input
      .flatMap(line => line.split("""\W+"""))
      .map(word => (word, 1))
      .reduceByKey((count1, count2) => count1 + count2)
      .saveAsTextFile(args(1))
    sc.stop()
  }
}
```
- 스칼라 API는 함수형 콤비네이터등이 데이터 분석 함수에 기본적으로 매핑되기 때문에 직관적으로 사용 가능

## 18.4 수학을 위한 카테고리

#### 모노이드(monoid)
- 특성
  1. 결합 법칙이 성립하는 이항 연산자 하나
  2. 항등원
- 수의 덧셈과 곱셈이 위의 특성을 만족
- 수의 덧셈과 곱셈은 교환 법칙도 만족하지만 모노이드에는 불필요
- 대부분의 데이터 구조가 이런 특성을 만족
  - 코드가 모노이드에 대해 작동하도록 코드를 일반화 가능하다면 재사용성 증가 - [위키피디아](http://bit.ly/1tphZ15)
- 예: 스트링 연결, 행렬 덧셈과 곱셈, 최소값/최대값 계산, HyperLogLog, Min-hash, Bloom filter
  - 일부는 교환 법칙도 만족
  - 거대한 데이터 집합에 대한 고성능 병렬 실행으로 구현 가능
  - 근사값 계산 알고리즘: 정확도를 낮추는 대신 공간 효율성을 극대화
  - [Add ALL The Things!](http://bit.ly/1wQpFNV)

## 18.5 스칼라 기반 데이터 도구 목록
> 하둡 플랫폼과 데이터베이스를 위한 스칼라 API외에 일반적인 수학이나 기계학습 관련 도구 소개

##### [Algebird](http://bit.ly/10Fk2F7)
- 대부분의 빅데이터 API와 사용 가능한 추상 대수 API
- twitter 개발

##### [Factorie](http://factorie.cs.umass.edu)
- 관계형 요소 그래프(relational factor graph)
- 매개변수 추정, 추론등을 위한 간결한 언어를 제공하는 배포 가능한 **확률적 모델링** 을 위한 툴킷

##### [Figaro](http://bit.ly/1nWnQf4)
- **확률적 프로그래밍** 을 위한 툴킷

##### [H2O](http://bit.ly/1G2rfz5)
- 데이터 분석을 위한 고성능 인메모리 분산 계산 엔진
- 자바 기반(R, Scala API 제공)

##### [Relate](http://bit.ly/13p17zp)
- 성능에 초점을 맞춘 얇은 데이터베이스 접근 계층

##### [ScalaNLP](http://scalanlp.org)
- 기계학습 & 수치계산 라이브러리
- 한 프로젝트 내 여러 라이브러리 포함
  - [Breeze](http://bit.ly/1q8K1uq): 기계학습과 수치계산
  - [Epic](http://bit.ly/1wNX2iJ): 통계 데이터 구문분석 및 구조적인 예측

##### [ScalaStorm](http://bit.ly/10aaroq)
- 스톰을 위한 스칼라 API

##### [Scalding](http://github.com/twitter/scalding)
- [Cascading](http://cascading.org)을 바탕으로 트위터에서 구축한 API
- 스칼라를 하둡 프로그래밍에 유명한 언어로 만든 장본인

##### [Scoobi](https://github.com/nicta/scoobi)
- 맵리듀스 위에 구축한 스칼라 추상화 계층
- 스파크나 스캘딩과 비슷

##### [Slick](http://slick.typesafe.com)
- 데이터베이스 접근 계층
- 타입세이프에서 개발

##### [Spark](http://spark.apache.org)
- 하둡 환경에서 분산계산을 수행
- [Mesos](http://mesos.apache.org) 클러스터, 로컬모드 모두에서 실행 가능

##### [Spire](http://github,com/non/spire)
- 빠르고 정확하며 제네릭한 수치 계산 라이브러리

##### [Summingbird](http://github.com/twitter/summingbird)
- 스캘딩(일괄처리)과 스톰(스트리밍) 위의 계산을 추상화한 API
- twitter에서 개발


## 18.6 마치며
#### 배운 것
- **빅데이터** 에서의 스칼라의 가능성은 독보적
- 스캘딩 & 스칼라로 인해 데이터 중심의 애플리케이션 개발에 자연스럽게 스칼라 사용이 채택될 수 있음

#### 배울 것
- 스칼라는 자바와 마찬가지로 정적 타입 언어
- 스칼라에 루비나 파이썬처럼 좀 더 동적으로 작동하는 타입을 만들 수 있는 트레이트 존재
  - **도메인 특화 언어**(DSL, domain-specific language)를 만들 수 있게 해주는 기능 중 하나 
