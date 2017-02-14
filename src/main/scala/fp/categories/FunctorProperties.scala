package fp.categories

import org.scalatest.Fact.Binary_&
import org.scalatest.FunSpec
import org.scalatest.prop.PropertyChecks


class FunctorProperties extends FunSpec with PropertyChecks{
  def id[A] = identity[A]_

  def testSeqMorphism(f2: Int => Int) = {
    val f1: Int => Int = _ * 2
    import SeqF._
    forAll { (l: List[Int]) =>
      assert( map(map(l)(f1))(f2) === map(l)(f2 compose f1) )
    }
  }

  def testFunctionMorphism(f2: Int => Int) = {
    val f1: Int => Int = _ * 2
    import FunctionF._
    forAll { (i: Int) =>
      assert(map(f1)(f2)(i) === (f2 compose f1) (i))
    }
  }

  describe ("Functor morphism composition") {
    it ("works for Sequence Functors") {
      testSeqMorphism(_ + 3)
    }
    it ("works for Function Functors") {
      testFunctionMorphism(_ + 3)
    }
  }

  describe ("Functor identity composed with a another function commutes") {
    it ("works for Sequence Functors") {
      testSeqMorphism(id[Int])
    }
    it ("works for Function Functors") {
      testFunctionMorphism(id)
    }
  }

  describe ("Functor identity maps between the identities of the categories") {
    it ("works for Sequence Functors") {
      val f1: Int => String = _.toString
      import SeqF._
      assert( map(List.empty[Int])(f1) === List.empty[String] )
    }
    it ("works for Function Functors") {
      val f1: Int => Int = _ * 2
      def id[A] = identity[A] _ // lift method to function
      import FunctionF._
      forAll { (i: Int) =>
        assert( map(id[Int])(f1)(i) === (f1 compose id[Int])(i) )
      }
    }
  }

  describe ("Function morphism composition is associative") {
    it ("works for Sequence Functors") {
      val f1: Int => Int = _ * 2
      val f2: Int => Int = _ + 3
      val f3: Int => Int = _ * 5
      import SeqF._
      forAll { (l: List[Int]) =>
        val m12 = map(map(l)(f1))(f2)
        val m23 = (seq: Seq[Int]) => map(map(seq)(f2))(f3)
        assert( map(m12)(f3) === m23(map(l)(f1)) )
      }
    }
    it ("works for Function Functors") {
      val f1: Int => Int = _ * 2
      val f2: Int => Int = _ + 3
      val f3: Int => Int = _ * 5
      val f : Int => Int = _ + 21
      import FunctionF._
      val m12 = map(map(f)(f1))(f2)
      val m23 = (g: Int => Int) => map(map(g)(f2))(f3)
      forAll { (i: Int) ->
        assert( map(m12)(f3)(i) === m23(map(f)(f1))(i) )
      }
    }
  }
}
