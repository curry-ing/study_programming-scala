package objectsystem.variance

class CSuper                { def msuper() = println("CSuper")}
class C      extends CSuper { def m()      = println("C")     }
class CSub   extends C      { def msub()   = println("CSub")  }