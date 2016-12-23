package traits.ui2

import traits.observer.Subject

trait ObservableClicks extends Clickable with Subject[Clickable] {
  abstract override def click(): Unit = {     // 1. `abstract` 키워드
    super.click()
    notifyObservers(this)
  }
}
