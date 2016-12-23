val seq = Seq("1")

val list = List("1", "2", "3", "4")

val map = Map("a"->1, "b"->2)

((1 to 10) fold "") ((s1, s2) => s"$s1 - $s2")

((1 to 10).par fold "") ((s1, s2) => s"$s1 - $s2")


