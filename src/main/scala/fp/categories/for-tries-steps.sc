import scala.util.{Failure, Success, Try}

type Step = Int => Try[Int]

val successfulSteps: Seq[Step] = List(
  (i: Int) => Success(i + 5),
  (i: Int) => Success(i + 10),
  (i: Int) => Success(i + 25)
)

val partiallySuccessfulSteps: Seq[Step] = List(
  (i: Int) => Success(i + 5),
  (i: Int) => Failure(new RuntimeException("FAIL!")),
  (i: Int) => Success(i + 5)
)

def sumCounts(countSteps: Seq[Step]): Try[Int] = {
  val zero: Try[Int] = Success(0)
  (countSteps foldLeft zero) {
    (sumTry, step) => sumTry flatMap(i => step(i))
  }
}

sumCounts(successfulSteps)
sumCounts(partiallySuccessfulSteps)
