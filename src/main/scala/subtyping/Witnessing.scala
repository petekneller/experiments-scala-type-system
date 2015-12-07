package subtyping

object Witnessing {

  class X
  class Y extends X
  class Z extends Y


  trait EqualTo[A, B]
  implicit def ev[A] = new EqualTo[A, A] {}


  def doSomething[A, B](a: A, b: B)(implicit ev: EqualTo[A, B]) = ???

  // should compile
  doSomething(new Y, new Y)
  // should not compile
  //doSomething(new X, new Y) // there is no A for which EqualTo[A, A] is a subtype of EqualTo[X, Y]
  // also should not compile
  //doSomething(new Z, new Y) // since EqualTo is invariant in both positions



  trait SubtypeOf[A, +B]
  implicit def ev2[A] = new SubtypeOf[A, A] {}

  def doSomething2[A, B](a: A, b: B)(implicit ev: SubtypeOf[A, B]) = ???

  // should compile
  doSomething2(new Y, new Y)
  // should compile
  doSomething2(new Z, new Y)
  // should not compile
  //doSomething2(new X, new Y) // as there is no A for which SubtypeOf[A, A] is a subtype of SubtypeOf[X, Y]

  // incidentally, scala predef uses:
  // trait SubtypeOf[-A, +B]
  // ... contravariance in the first position as well; but it isnt needed in my example
}
