package subtyping

object StructuralSubtyping {

  class A {
    def foo(i: Int): Boolean = ???
    def bar(i: String): Double = ???
  }

  type B = { def foo(i: Int): Boolean }


  val a: A = new A
//  val b: B = new { def foo(i: Int): Boolean = ??? }
//  val b: B = new B { def foo(i: Int): Boolean = ??? }

  val c: B = new A
  val d: { def foo(i: Int): Boolean } = new A



  def f1(b: B): B = identity(b)

  val f1a: B = f1(new A)
//  val f1b: B = f1(new B { def foo(i: Int): Boolean = ??? })

  def f2[T <: B](t: T): T = identity(t)

//  val f2b: B = f2(new B { def foo(i: Int): Boolean = ??? })
//  val f2a: A = f2(new B { def foo(i: Int): Boolean = ??? })
  val f2a: A = f2(new A)


}
