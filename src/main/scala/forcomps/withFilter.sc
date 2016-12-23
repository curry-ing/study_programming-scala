val list = List(1,2,3,4,5,6,7,8,9,10)

list.filter(_ % 2 == 0) // List를 반환

list.withFilter(_ % 2 == 0) // TraversableLike 객체를 반환

list.withFilter(_ % 2 == 0).withFliter(true)