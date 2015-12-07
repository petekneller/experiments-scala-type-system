package implicitResolution.parameters

trait Baz

object Baz {
  implicit val f6 = new Foo[Baz]
}

trait Bar

object Bar {
  // 2-1. implicits in companion object of super type of destination type
//  implicit val f5 = new Foo
}

class Foo[A] extends Bar

object Foo {
  // 2-1. implicits in companion object of destination/expected type
//  implicit val f4 = new Foo[Int]
}

object Foos2 {
  implicit val f2 = new Foo[Baz]
}

object Foos3 {
  implicit val f3 = new Foo[Baz]
}

object ImplicitParameters {

  // 1-1. implicit in scope
  implicit val f1 = new Foo[Baz]

  // 1-2. explicit imports
//  import Foos2.f2

  // 1-3. wildcard imports
  //import Foos3._

//  def needsAFoo(a: Int)(implicit foo: Foo[Int]): Unit = ???
  def needsAFoo(a: Int)(implicit foo: Foo[Baz]): Unit = ???

  needsAFoo(1)
}
