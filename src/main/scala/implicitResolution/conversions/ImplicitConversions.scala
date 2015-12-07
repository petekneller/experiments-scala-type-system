package implicitResolution.conversions

class Woo

object Woo {
  // 2-2 implicit scope (companions, superclass companions) of arguments
  implicit def f6(baz: Baz): Foo = ??? // supposedly, but doesn't seem to work
}

trait Foo {
  def foo(woo: Woo): Unit = ???
}

trait Bar

object Bar {
  // 2-1. companion of super type of source type
//    implicit def f5(bar: Baz): Foo = ???
}

class Baz extends Bar

object Baz {
  // 2-1. companion of source type
//  implicit def f4(baz: Baz): Foo = ???
}

object Foos2 {
  implicit def f2(a: Baz): Foo = ???
}

object Foos3 {
  implicit def f3(a: Baz): Foo = ???
}

object ImplicitConversions {

  // 1-1. local scope
  implicit def f1(a: Baz): Foo = ???

  // 1-2. explicit imports
//  import Foos2.f2

  // 1-3. wildcard imports
//  import Foos3._

  val b = (new Baz).foo(new Woo)

}
