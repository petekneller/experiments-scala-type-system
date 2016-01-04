package GADTs

object Failings {

  // A comment from Paul's blog post: http://pchiusano.blogspot.co.uk/2010/06/gadts-in-scala.html
  // pointing out a limitation of Scala's type inf

  /* This doesn't compile

  trait Eq[A,B]
  case class Refl[A]() extends Eq[A,B]

  object Main {
    def eval[A](a : A, eq : Eq[A,Int]) : A = eq match {
      case Refl() => 1 + a
    }
  }
  */

  /* Versus in haskell:

    data Equal a b where
      Refl :: Equal a a

    eval :: a -> Equal a Int -> a
    eval x Refl = 1 + x

  */

  // Further example at http://lambdalog.seanseefried.com/posts/2011-11-22-gadts-in-scala.html

}
