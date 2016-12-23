package traits.ui2                  // 0 traits.ui에 Clickable이 이미 있기에 새로운 패키지를 선언

trait Clickable {
  def click(): Unit = updateUI()    // 1 공개 메서드인 click은 여기서는 구체적 메서드. (`updateUI`에 모든것을 위임)

  protected def updateUI(): Unit    // 2 보호 메서드 & 추상 메서드: 구현 클래스에서는 이 클래스에 대한 로직을 정의 필요
}


