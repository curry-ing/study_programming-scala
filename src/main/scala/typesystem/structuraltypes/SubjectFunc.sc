import typesystem.structuraltypes.SubjectFunc

val observer: Int => Unit = (state: Int) => println("got one! " + state)

val subject = new SubjectFunc { ??? }