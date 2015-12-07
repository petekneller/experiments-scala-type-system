package catamorphisms

import org.scalatest.FunSuite

class IterationTest extends FunSuite {

  test("iteration") {

    type StepAlgebra[B] = (B, B => B)

    sealed trait Nat
    case object Zero extends Nat
    case class Succ(of: Nat) extends Nat

    def foldSteps[B](alg: StepAlgebra[B], n: Nat): B = {
      val (nil, next) = alg
      n match {
        case Zero => nil
        case Succ(nat) => next(foldSteps(alg, nat))
      }
    }

    val waitGo = ("go!", (s: String) => "wait..." + s)
    val four = Succ(Succ(Succ(Succ(Zero))))

    assert(foldSteps(waitGo,  four) === "wait...wait...wait...wait...go!")

  }

}
