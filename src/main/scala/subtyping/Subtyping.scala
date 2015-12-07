package subtyping

object Subtyping {

  class X
  class Y extends X
  class Z extends Y

  class A { def doSomething(y: Y): Y = ??? }
  class B extends A


  // a variable can ref an object of its own type
  val v1: A = new A

  // a variable can ref an object of a more specific type
  val v2: A = new B

  // a subclass is a subtype if it doesn't override, or overrides but doesn't modify the signature of, any methods
  class C extends B { override def doSomething(y: Y): Y = ??? }
  val v3: A = new C

  // a subclass is a subtype if it overrides a method and changes the signature, such that return type is covariant (more specific)
  class C1 extends B { override def doSomething(y: Y): Z = ??? }

  // the JVM does not allow method args to be contravariant, so the following does not compile
  //class C2 extends B { override def doSomething(y: Z): Y = ??? }


  class Foo[T]

  // by default, type params are invariant, so a parameterised type that differs in its parameters from the type of
  // the variable is not a subtype, hence not assignable to
  val v4: Foo[Y] = new Foo[Y]
  // therefore the following does not compile
  //val v5: Foo[Y] = new Foo[X]
  //val v6: Foo[Y] = new Foo[Z]

  class Bar[T] extends Foo[T]

  // concrete types that are subtypes of the type of the variable are assignable...
  val v7: Foo[Y] = new Bar[Y]
  // but type params are still invariant
  //val v8: Foo[Y] = new Bar[Z]

//  class Baz[+T] extends Foo[T]

  // variance modifiers are checked in the defn of the abstract type of the _variable_, not the type of the object being assigned
  // so this doesn't compile
  //val v9: Foo[Y] = new Baz[Z] // because Baz[+T] can't 'override' the invariance of T in Foo
  // but this does
//  val v10: Baz[Y] = new Baz[Z]
  // and this
//  class Bazza[T] extends Baz[T]
//  val v11: Baz[Y] = new Bazza[Z]
  // the following doesn't compile, as I've removed the variance annotation from the subtype, which is now the type of the var
  //val v12: Bazza[Y] = new Bazza[Z]


  // Scala lets function types support argument contra- and return covariance through type annotations
  class MyFunc[-A, +B] { def myApply(a: A): B = ??? }

  // the lambda { (a: Int) => a.toString } is effectively:
  class F1 extends MyFunc[Int, String] { override def myApply(a: Int): String = ??? }
  val v13: MyFunc[Int, String] = new F1

  // contravariant in the args
  val v14: MyFunc[Y, Y] = new MyFunc[X, Y]
  // covariant in the return
  val v15: MyFunc[Y, Y] = new MyFunc[Y, Z]
  // both
  val v16: MyFunc[Y, Y] = new MyFunc[X, Z]

  // Some examples of how these things affect the inferencing engine?

}
