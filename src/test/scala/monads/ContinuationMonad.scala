package monads

import org.scalatest.{Matchers, FunSuite}

class ContinuationMonad extends FunSuite with Matchers {

  def square: Int => Int = a => a * a
  def double: Int => Int = a => 2 * a
  def plusSeven: Int => Int = a => a + 7

  val input = 2
  val expected = 121

  test("a simple un-monadic, non-cps calculation") {
//    val r = square(plusSeven(double(input)))
    val r = (double andThen plusSeven andThen square)(input)
    r should be(expected)
  }

  // value argument first
  def squareCps1[T]: (Int, (Int => T)) => T = (a, f) => f(a * a)
  def doubleCps1[T]: (Int, (Int => T)) => T = (a, f) => f(2 * a)
  def plusSevenCps1[T]: (Int, (Int => T)) => T = (a, f) => f(a + 7)

  // continuation first
  def squareCps2[T]: ((Int => T), Int) => T = (f, a) => f(a * a)
  def doubleCps2[T]: ((Int => T), Int) => T = (f, a) => f(2 * a)
  def plusSevenCps2[T]: ((Int => T), Int) => T = (f, a) => f(a + 7)

  test("a simple un-monadic, but cps'd, calculation") {
    val r1 = doubleCps1(input, a => plusSevenCps1(a, b => squareCps1(b, identity)))
    r1 should be(expected)

    val r2 = doubleCps2(a => plusSevenCps2(b => squareCps2(identity, b), a), input)
    r2 should be(expected)
  }

  test("a custom combinator") {
    type CPS_F[A, B] = (A => B) => B
    implicit class CpsOps[A, R](c1: CPS_F[A, R]) {
      def into[B](f: A => CPS_F[B, R]): CPS_F[B, R] = (c2: B => R) => {
        c1(a => f(a)(c2))
      }
    }

    // what I'd like to type:
    /*
    val r = doubleCps1(input, _) into
      (a => plusSevenCps1(a, _)) into
      (b => squareCps1(b, identity))
    */

    // what I need to do:
    val f1: Int => (Int => Int) => Int = (a: Int) => (cont: Int => Int) => doubleCps1(a, cont)
    val f2: Int => (Int => Int) => Int = (a: Int) => (cont: Int => Int) => plusSevenCps1(a, cont)
    val f3: Int => (Int => Int) => Int = (a: Int) => (cont: Int => Int) => squareCps1(a, cont)

    val r = (new CpsOps[Int, Int](f1(input)) into
      ((a: Int) => f2(a)) into
      ((b: Int) => f3(b)))(identity)

    r should be(expected)
  }

  test("a simple monadic cps calculation") {
    /*
    // what I'd like to type:
    val rm = for {
      a <- doubleCps2
      b <- plusSevenCps2
      c <- squareCps2
    } yield c
    */

    /*
    case class ContMonad[A, B](f: (A => B) => B) {
      def map[C](f: B => C): ContMonad[A, C, R] = ???
      def flatMap[C](f2: B => ContMonad[B, C, R]): ContMonad[A, C, R] = ContMonad{
        (f3, v) =>
          f({b =>
            f2(b)
          }, v)
      }
    }

    def lift[A, B, C](f: ((B => C), A) => C): ContMonad[A, B, C] = ContMonad(f)

    val rm: ContMonad[Int, Int] = for {
      a <- lift[Int, Int](doubleCps2(input))
      b <- lift[Int, Int](plusSevenCps2)(a)
      c <- lift[Int, Int](squareCps2)(b)
    } yield c

    rm.f(identity) should be(expected)
    */
  }

}
