package GADTs

// From Paul's original post about GADTs in Scala here: http://pchiusano.blogspot.co.uk/2010/06/gadts-in-scala.html

object PaulChiusano1 {

  trait Expr[A]
  case class I(i: Int) extends Expr[Int]
  case class B(b: Boolean) extends Expr[Boolean]
  case class Add(a: Expr[Int], b: Expr[Int]) extends Expr[Int]

  def eval[A](e: Expr[A]): A = e match {
    case I(i) => i
    case B(b) => b
    case Add(a,b) => eval(a) + eval(b)
  }

  // This is a type error, as desired
  // Add(I(1), B(false))

  eval(I(22)) // results in 22, etc.


  // A comment from the post by Luke Palmer pointing at a failing

  trait Eq[A,B]
  case class Refl[A]() extends Eq[A,A]

  object Main {
    // This doesn't compile
    // def eval[A](a : A, eq : Eq[A,Int]) : A = eq match {
    //   case Refl() => 1 + a
    // }
  }

  /* Versus in haskell:

    data Equal a b where
      Refl :: Equal a a

    eval :: a -> Equal a Int -> a
    eval x Refl = 1 + x

  */

}
