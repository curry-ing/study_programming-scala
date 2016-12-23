class CSuper                { def msuper() = println("CSuper")}
class C      extends CSuper { def m()      = println("C")     }
class CSub   extends C      { def msub()   = println("CSub")  }

//import objectsystem.variance._

var f: (C => C) = (c: C)      => new C
    f           = (c: CSuper) => new CSub
    f           = (c: CSuper) => new C
//    f           = (c: CSub)      => new CSub
    f           = (c: CSub) => new CSuper
