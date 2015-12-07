package funWithTypeFunctions

import org.scalatest.FunSuite
import Example1Arithmetic._

class Example1ArithmeticTest extends FunSuite {
  
  test("Num typeclass") {
    def double[A: Num](a: A): A = implicitly[Num[A]].plus(a, a)
    
    assert(double(2: Int) === (4: Int))
    assert(double(2: Double) === (4: Double))
  }  
  
  test("Add typeclass") {
    def f[A, B](a: A, b: B)(implicit ev: Add[A, B]): Add[A, B]#ResType = implicitly[Add[A, B]].add(a, b)
    
    assert(f(2: Int, 2: Int) === (4: Int))
    assert(f(2: Double, 2: Int) === (4: Double))
    assert(f(2: Int, 2: Double) === (4: Double))
  }

}
