package pk

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

class UnboxedUnionTypesTest extends FunSuite with ShouldMatchers {

  test("foo") {

    mySize(2) should be(2)
    mySize("foo") should be(3)
    // mySize(2: Double) // shouldn't compile


  }

  type ¬[A] = A => Nothing

  type ¬¬[A] = ¬[¬[A]]

  type ∨[T, U] = ¬[¬[T] with ¬[U]]

  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }

  def mySize[T : (Int |∨| String)#λ](t : T) = t match {
    case i : Int => i
    case s : String => s.length
  }

}
