import traits.ui2._
import traits.observer._

val button =
  new Button("Click Me!") with ObservableClicks with VetoableClicks {
    override val maxAllowed = 2                           // 1. `maxAllowed`를 2로 overriding
  }
// val button = new Button("Click Me!") with VetoableClicks with ObservableClicks

class ClickCountObserver extends Observer[Clickable] {    // 2. ClickObserver는 이전 예제와 동일
  var count = 0
  def receiveUpdate(state: Clickable): Unit = count += 1
}

val bco1 = new ClickCountObserver
val bco2 = new ClickCountObserver

button addObserver bco1
button addObserver bco2

(1 to 5) foreach (_ => button.click())

println(bco1.count)                                        // 3. 실제 클릭 횟수는 5이지만 이 예제에서는 2 이상 올라가지 않는다
println(bco2.count)