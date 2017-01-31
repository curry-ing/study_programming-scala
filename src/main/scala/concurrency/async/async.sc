import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global

object AsyncExample {
  def recordExists(id: Long): Boolean = {   // 레코드의 존재여부 검사 (`id > 0`인 경우 참)
    println(s"recordExists($id)...")
    Thread.sleep(1)   // 높은 비용
    id > 0
  }

  def getRecord(id: Long): (Long, String) = { // id에 해당하는 레코드를 가져온다
    println(s"getRecord($id)...")
    Thread.sleep(1)   // 높은 비용
    (id, s"record: $id")
  }

  def asyncGetRecord(id: Long): Future[(Long, String)] = async {  // 비동기 연산을 함께 순서대로 수행
    val exists = async {        // recordExists를 '비동기적'으로 호출
      val b = recordExists(id)
      println(b)
      b
    }
    if (await(exists)) await(async {  // 결과가 반환되기를 기다렸다가 참인경우 레코를 비동기적으로 꺼내온다
      val r = getRecord(id)
      println(r)
      r
    })
    else (id, "Record not found!")    // 반환된 결과가 거짓인 경우 오류
  }
}

(-1 to 1) foreach { id =>
  val fut = AsyncExample.asyncGetRecord(id)
  println(Await.result(fut, Duration.Inf))
}
