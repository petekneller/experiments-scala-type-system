package scalazMonadStack

import org.scalatest.{Matchers, FunSuite}

import scalaz.{Id, Kleisli, Reader}

class ChangingTypeOfReader extends FunSuite with Matchers {

  type Env1 = Map[String, String]
  val env1 = Map("a" -> "foo", "b" -> "bar")

  val capitalizeAll: Reader[Env1, Env1] = for {
    littleEnv <- Kleisli.ask[Id.Id, Env1]
  } yield littleEnv.mapValues(_.toUpperCase)

  test("a function that makes a simple transformation to all values in the environment") {
    capitalizeAll.run(env1) should be(Map("a" -> "FOO", "b" -> "BAR"))
  }

  type Env2 = Map[String, Map[String, String]]
  val env2 = Map(
    "dev" -> env1,
    "prod" -> Map("a" -> "baz", "b" -> "quux")
  )

  test("sometimes having access to the entire environment is too much") {
    // Hmmm, can't do this any more
    //capitalizeAll.run(env2) should be(Map("a" -> "FOO", "b" -> "BAR"))
  }

  /*
   Could always just extract the (Env1 => Env1) part of capitalizeAll and
   then use that both in capitalizeAll and another Reader function that works
   only on a subpart of Env2
  */

  test("you can lift a Reader of A into a Reader of B as long as there is a (B => A)") {

    def subEnv(env: Env2): Env1 = env("dev")
    def liftR[A, E1, E2](f: E1 => E2)(reader: Reader[E2, A]): Reader[E1, A] = Reader{
      env1 =>
        reader.run(f(env1))
    }

    liftR(subEnv)(capitalizeAll).run(env2) should be(Map("a" -> "FOO", "b" -> "BAR"))
  }

}
