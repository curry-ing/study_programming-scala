package traits.ui2

trait VetoableClicks extends Clickable {  // 1. `Clickable`의 확장
  val maxAllowed = 1                      // 2. 허용하는 눌림 횟수의 최댓값 ('재설정' 기능 등이 있다면 유용)
  private var count = 0

  abstract override def click() = {
    if (count < maxAllowed) {             // 3. 클릭 횟수가 지정한 최댓값을 넘어가면, 더 이상의 이벤트를 `super`에 보내지 않음
      count += 1
      super.click()
    }
  }
}
