package heikoSeeberger

import org.scalacheck.Prop
import org.specs2.mutable.Specification
import org.specs2.{ScalaCheck}

// from Heiko Seebergers blog post about category theory in Scala
// http://hseeberger.wordpress.com/2010/11/25/introduction-to-category-theory-in-scala/

object Categories {

  object Category {
    def id[A]: A => A = a => a

    def compose[A, B, C](g: B => C, f: A => B): A => C =
      g compose f // This is Function.compose, not a recursive call!
  }


  class CategorySpec extends Specification with ScalaCheck {

    import Category._

    "A Category" should {

      val f = (i: Int) => i.toString
      val g = (s: String) => s.length
      val h = (i: Int) => i * i

      "satisfy associativity" in {
        Prop forAll { (i: Int) =>
          compose(h, compose(g, f))(i) == compose(compose(h, g), f)(i)
        }
      }

      "satisfy identity" in {
        Prop forAll { (i: Int) =>
          compose(f, id[Int])(i) mustEqual compose(id[String], f)(i)
        }
      }
    }
  }


  trait GenericCategory[->>[_, _]] {
    def id[A]: A ->> A

    def compose[A, B, C](g: B ->> C, f: A ->> B): A ->> C
  }


  object Category2 extends GenericCategory[Function] {
    def id[A]: A => A = a => a

    def compose[A, B, C](g: B => C, f: A => B): A => C =
      g compose f // This is Function.compose, not a recursive call!
  }

  // Functors

  trait GenericFunctor[->>[_, _], ->>>[_, _], F[_]] {
    def fmap[A, B](f: A ->> B): F[A] ->>> F[B]
  }


  trait Functor[F[_]] extends GenericFunctor[Function, Function, F] {
    final def fmap[A, B](as: F[A])(f: A => B): F[B] =
      fmap(f)(as)
  }


  object ListFunctor extends Functor[List] {
    def fmap[A, B](f: A => B): List[A] => List[B] = as => as map f
  }


  object Functor {

    def fmap[A, B, F[_]](as: F[A])(f: A => B)(implicit functor: Functor[F]): F[B] =
      functor.fmap(as)(f)

    implicit object ListFunctor extends Functor[List] {
      def fmap[A, B](f: A => B): List[A] => List[B] =
        as => as map f
    }

  }


  class ListFunctorTest extends Specification with ScalaCheck {

    import Functor.ListFunctor._

    "A ListFunctor" should {

      "preserve identity" in {
        val stringID = (s: String) => s
        val stringListID = (ss: List[String]) => ss
        Prop forAll { (ss: List[String]) =>
          fmap(stringID)(ss) == stringListID(ss)
        }
      }

      "preserve composition" in {
        val f = (i: Int) => i.toString
        val g = (s: String) => s.length
        Prop forAll { (is: List[Int]) =>
          fmap(g compose f)(is) == (fmap(g) compose fmap(f))(is)
        }
      }
    }
  }


  implicit object OptionFunctor extends Functor[Option] {
    def fmap[A, B](f: A => B): Option[A] => Option[B] =
      o => o map f
  }


  implicit object Function0Functor extends Functor[Function0] {
    def fmap[A, B](f: A => B): Function0[A] => Function0[B] =
      a => () => f(a())
  }

}
