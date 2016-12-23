val results: Seq[Option[Int]] = Vector(Some(10), None, Some(20))

val results2 = for {
  Some(i) <- results // 'results'원소와 매치되면서 None을 제거하고 Some안의 정수만 뽑아낸다
} yield (2 * i)

// 변환 단계 #1
val results2b = for {
  Some(i) <- results withFilter {
    case Some(i) => true
    case None => false
  }
} yield (2 * i)

// 변환 단계 #2
val results2c = results withFilter {
  case Some(i) => true
  case None => false
} map {
  case Some(i) => (2 * i)
}

