import scala.language.higherKinds
import typesystem.higherKinded.{Add, Reduce}
import typesystem.higherkinded.Add._
import typesystem.higherkinded.Reduce._

def sum[T: Add, M[T]](container: M[T])(implicit red: Reduce[T, M]): T =
  red.reduce(container)(implicitly[Add[T]].add(_,_))

sum(Vector(1->10, 2->20, 3->30))
sum(1 to 10)
sum(Option(2))
sum[Int, Option](None)    // error: `sum(reduce)`를 빈 컨테이너에 적용 불가
