package traits.observer

trait Observer[-State] {
  def receiveUpdate(state: State): Unit               // 1
}

trait Subject[State] {                                // 2
  private var observers: List[Observer[State]] = Nil  // 3

  def addObserver(observer: Observer[State]): Unit =  // 4
    observers ::= observer                            // 5

  def notifyObservers(state: State): Unit =           // 6
    observers foreach (_.receiveUpdate(state))
}
