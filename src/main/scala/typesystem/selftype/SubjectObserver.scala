package typesystem.selftype

abstract class SubjectObserver {
  type S <: Subject
  type O <: Observer

  trait Subject {
    self: S =>  // 1. `Subject`가 실제로 `Subject`를 혼합한 구체적 클래스인 스스로의 서브타입 `S`의 인스턴스가 될 것이라고 가정
    private var observers = List[O]()

    def addObserver(observer: O): Unit =
      observers ::= observer

    def notifyObservers(): Unit =
      observers.foreach(_.receiveUpdate(self))  // 2. this 대신 self를 receiveUpdate에 전달
  }

  trait Observer {
    def receiveUpdate(subject: S)
  }
}
