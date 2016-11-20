package dependentTypes


object PathDependents {

  /*
   Any type this is defined within the scope of another type can/will have a type that is unique to the enclosing instance
   */

  class X {
    class Y
  }

  val a = new X
  val b = new X
  val c = new a.Y
  // val d: b.Y = c // doesn't compile - a.Y is not the same as a b.Y

  val e: X#Y = c // but this does - a.Y is a subtype of X#Y
  // NB. that you can't the below notation, you need the #
  // val e: X.Y = c

  // Interestingly, not that the can't instantiate a type from the X 'level'
  // val f = new X#Y // doesn't compile

  def foo(x: X): X#Y = new x.Y
  // val g: b.Y = foo(b) // not specific enough
  def foo2(x: X): x.Y = new x.Y
  val g: b.Y = foo2(b)

}



/*
 We use this pattern all the time, where we _would_ just use a package for namespacing, but we have type aliases, functions, that we want at the top level and so can't use a raw package
 */
object Packaging {

  object MyPackage {
    // type members
    // functions
    class Foo
  }
  import MyPackage._
  val a = new Foo

  // We don't realise it but we're using path-dependent types even in the example above. It's just that since we're using an object as the 'package' we don't notice it.

  // If we were instead trying to parameterize our package - maybe call it a 'module' at this point
  trait MyModule[A] {
    // type members
    // functions
    class Foo
  }
  val MyStringModule = new MyModule[String] {}
  // import myModule._ // would do this, but that'd make Foo ambiguous in this example
  val b = new MyStringModule.Foo

  val MyIntModule = new MyModule[Int] {}
  val c = new MyIntModule.Foo

}
