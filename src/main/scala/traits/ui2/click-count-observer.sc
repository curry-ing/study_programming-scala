import traits.ui2._
import traits.observer._

val button = new Button("Click Me!") with ObservableClicks

class ClickCountObserver extends Observer[Clickable] {
  var count = 0
  def receiveUpdate(state: Clickable): Unit = count += 1
}

val bco1 = new ClickCountObserver
val bco2 = new ClickCountObserver

button addObserver bco1
button addObserver bco2

(1 to 5) foreach (_ => button.click())

println(bco1.count)
println(bco2.count)
