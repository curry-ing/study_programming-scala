import typesystem.structuraltypes.Subject

val subject = new Subject {
  type State = Int
  protected var count = 0
  def increment(): Unit = {
    count += 1
    notifyObservers(count)
  }
}

/* --- */

trait Logging {
  def log(message: String): Unit = println(s"Log: $message")
}

val subject2 = new Subject with Logging {
  type State = Int
  protected var count = 0
  def increment(): Unit = {
    count += 1
    notifyObservers(count)
  }
}
