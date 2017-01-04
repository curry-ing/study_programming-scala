import typesystem.higherkinded.Add
import typesystem.higherkinded.Add._

def sumSeq[T:Add](seq: Seq[T]): T =
  seq reduce (implicitly[Add[T]].add(_,_))

sumSeq(Vector(1->10, 2->20, 3->30))
sumSeq(1 to 10)
//sumSeq(Option(2))     // error: `Option`은 `Seq`의 서브타입이 아님
