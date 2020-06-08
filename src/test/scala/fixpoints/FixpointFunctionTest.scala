package fixpoints

import org.scalatest.{Matchers, FunSuite}

class FixpointFunctionTest extends FunSuite with Matchers {

  sealed trait Tree
  case class Node(l: Tree, r: Tree) extends Tree
  case class Leaf(i: Int) extends Tree

  // Sums the integers in the tree
  def sum1(t: Tree): Int = {
    t match {
      case Node(l, r) => sum1(l) + sum1(r)
      case Leaf(i) => i
    }
  }

  test("sum1") {
    sum1(Node(Leaf(1), Node(Leaf(2), Leaf(3)))) should be(6)
  }

  // Attempts to override behaviour of sum1 by first adding 1 to each leaf
  def add1_1(t: Tree): Int = {
    t match {
      case Leaf(i) => sum1(Leaf(i + 1))
      case n: Node => sum1(n)
    }
  }

  test("add1_1") {
    // Fails because only on the first call will add1_1 act
    // Once it calls sum1 it will not gain control again
    // add1_1(Node(Leaf(1), Node(Leaf(2), Leaf(3)))) should be(9)
  }

  // Second attempt - pass the recursive call explicitly so that mutual recursion can be done

  def sum2(f: => (Tree => Int)): Tree => Int = { t =>
    t match {
      case Node(l, r) => f(l) + f(r)
      case Leaf(i) => i
    }
  }

  test("sum2_naive") {
    // This clearly doesn't work; you can't call sum2 without passing the partially applied sum2, which requires the partially applied sum2 ....
    //sum2(sum2(sum2(sum2)))(Node(Leaf(1), Node(Leaf(2), Leaf(3)))) should be(6)
  }

  // the 'fixpoint operator/function' is the answer
  // it 'ties the recursive knot'
  // which in my words means that it captures one layer of the call stack so that it doesn't have to be explicitly provided
  def fix(f: (=> (Tree => Int)) => (Tree => Int)): Tree => Int = f(fix(f))

  test("sum2") {
    fix(sum2)(Node(Leaf(1), Node(Leaf(2), Leaf(3)))) should be(6)
  }

  def add1_2(f: => (Tree => Int)): Tree => Int = { t =>
    t match {
      case Leaf(i) => sum2(f)(Leaf(i + 1))
      case n: Node => sum2(f)(n)
    }
  }

  test("add1_2") {
    fix(add1_2)(Node(Leaf(1), Node(Leaf(2), Leaf(3)))) should be(9)
  }

}
