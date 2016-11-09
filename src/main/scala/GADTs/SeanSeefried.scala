package GADTs

object SeanSeefried {

  // Taken from http://lambdalog.seanseefried.com/posts/2011-11-22-gadts-in-scala.html

  // Canonial example in Haskell of why GADTs are useful
  // ---------------------------------------------------
  /*

   {-# LANGUAGE GADTs #-}
   module Exp where

   data Exp a where
     LitInt  :: Int                        -> Exp Int
     LitBool :: Bool                       -> Exp Bool
     Add     :: Exp Int -> Exp Int         -> Exp Int
     Mul     :: Exp Int -> Exp Int         -> Exp Int
     Cond    :: Exp Bool -> Exp a -> Exp a -> Exp a
     EqE     :: Eq a => Exp a -> Exp a     -> Exp Bool

   eval :: Exp a -> a
   eval e = case e of
     LitInt i       -> i
     LitBool b      -> b
     Add e e'       -> eval e + eval e'
     Mul e e'       -> eval e * eval e'
     Cond b thn els -> if eval b then eval thn else eval els
     EqE e e'       -> eval e == eval e'



   LitInt 1 `Add` LitBool True -- this expression does not type check

   */

  // Directly in Scala
  object Directly {

    abstract class Exp[A] {
      def eval: A = this match {
        // this doesn't compile
        // case LitInt(i)       => i
        // case LitBool(b)      => b
        // case Add(e1, e2)     => e1.eval + e2.eval
        // case Mul(e1, e2)     => e1.eval * e2.eval
        // case Cond(b,thn,els) => if ( b.eval ) { thn.eval } else { els.eval }
        // case Eq(e1,e2)       => e1.eval == e2.eval

        // or this
        // case i: Exp[Int] => 1

        case _ => throw new RuntimeException("placeholder")
      }

    }

    case class LitInt(i: Int)                                   extends Exp[Int]
    case class LitBool(b: Boolean)                                  extends Exp[Boolean]
    case class Add(e1: Exp[Int], e2: Exp[Int])                    extends Exp[Int]
    case class Mul(e1: Exp[Int], e2: Exp[Int])                    extends Exp[Int]
    case class Cond[A](b: Exp[Boolean], thn: Exp[A], els: Exp[A])   extends Exp[A]
    case class Eq[A](e1: Exp[A], e2: Exp[A])                  extends Exp[Boolean]
  }

  /*
  This is wierd, but apparently its because the case class constructors aren't polymorphic ie.
    LitInt :: Integer => Exp Integer
  where it needs to be
    LitInt :: Integer => Exp a
  in order for the pattern match to work. The Cond one will work, because is polymorphic in one type argument

   */

  // One way of fixing it would be to just fall back on OO: each subclass provides its own impl of eval. Sean has an example of this but I didn't copy it.

  // Or...
  // Apparently it works if you define eval in a polymorphic function, in which case the A is properly refined. Go figure.
  object TheFPWay {

    object Exp {
      def evalAny[A](e: Exp[A]): A = e match {
        case LitInt(i)         => i
        case LitBool(b)        => b
        case Add(e1, e2)       => e1.eval + e2.eval
        case Mul(e1, e2)       => e1.eval * e2.eval
        case Cond(b, thn, els) => if (b.eval) { thn.eval } else { els.eval }
        case Eq(e1, e2)        => e1.eval == e2.eval
      }
    }

    abstract class Exp[A] {
      def eval: A = Exp.evalAny(this)
    }

    case class LitInt(i: Int)                                     extends Exp[Int]
    case class LitBool(b: Boolean)                                extends Exp[Boolean]
    case class Add(e1: Exp[Int], e2: Exp[Int])                    extends Exp[Int]
    case class Mul(e1: Exp[Int], e2: Exp[Int])                    extends Exp[Int]
    case class Cond[A](b: Exp[Boolean], thn: Exp[A], els: Exp[A]) extends Exp[A]
    case class Eq[A](e1: Exp[A], e2: Exp[A])                      extends Exp[Boolean]
  }


}
