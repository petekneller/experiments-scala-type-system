package funWithTypeFunctions

/*
 * A port of the haskell impl in the corresponding src folder
*/

object Example1Arithmetic {

  // some basics that I dont have in scala
  trait Num[T] {
    def plus(a: T, b: T): T
  }

  implicit object IntNum extends Num[Int] {
    def plus(a: Int, b: Int) = a + b
  }

  implicit object DoubleNum extends Num[Double] {
    def plus(a: Double, b: Double) = a + b
  }

  // now the actual example
  trait Add[A, B] {
    type ResType // unlike in haskell, no type params necessary, since the trait 'captures' them
    def add(a: A, b: B): ResType
  }

  /* would like to do:
  implicit class NumAdd(implicit num: Num[A]) extends Add[A, A] {

  unfortunately can't use implicit class because they need a single cstr param
  (which mine does) but that param cant be implicit
  */
  class NumAdd[A](num: Num[A]) extends Add[A, A] {
    type ResType = A
    def add(a: A, b: A): ResType = num.plus(a, b)
  }
  implicit def numAdd[A: Num] = new NumAdd[A](implicitly[Num[A]])
  
  // or I can capture the Num instance statically as long as its avail in this scope
  implicit object IntDoubleAdd extends Add[Int, Double] {
    val num = implicitly[Num[Double]]
    type ResType = Double
    def add(a: Int, b: Double): ResType = num.plus(a.toDouble, b)
  }

  implicit object DoubleIntAdd extends Add[Double, Int] {
    val num = implicitly[Num[Double]]
    type ResType = Double
    def add(a: Double, b: Int): ResType = num.plus(a, b.toDouble)
  }
}
