package catamorphisms

import org.scalatest.FunSuite

class ListTest extends FunSuite {

  test("list") {

    type ContainerAlgebra[A, B] = (B, (A, B) => B)

    sealed trait List[+A]
    case object Nil extends List[Nothing]
    case class Cons[+A](a: A, l: List[A]) extends List[A]

    def foldrList[A, B](alg: ContainerAlgebra[A, B], l: List[A]): B = {
      val (nil, merge) = alg
      l match {
        case Nil          => nil
        case Cons(x, xs)  => merge(x, foldrList(alg, xs))
      }
    }

    val alg = (3, (x: Int, y: Int) => x * y)
    assert(foldrList(alg, Cons(10, Cons(100, Cons(1000, Nil)))) === 3000000)

  }

}
