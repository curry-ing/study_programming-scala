case object Foo { override def toString = "Foo says Hello!" }

def printFoo(foo: Foo.type) = println(foo)

printFoo(Foo)
