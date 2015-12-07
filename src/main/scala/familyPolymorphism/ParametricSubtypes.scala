package familyPolymorphism

object ParametricSubtypes {

  // can't be used parametrically
  trait FooInterface {
    def modifyMe(x: Int): FooInterface
  }

  class OnePossibleFooImpl extends FooInterface {
    def modifyMe(x: Int): FooInterface = ???
  }

  trait UsesFooParametrically[A <: FooInterface] {

    // the following doesn't compile because:
    // modifyMe returns a Foo, so you can't then type it to A
    //    def doSomething(a: A): A = {
    //      a.modifyMe(2)
    //    }
  }

  // one solution (using type param)
  // -------------------------------

  trait BarInterface[T <: BarInterface[T]] {
    def modifyMe(x: Int): T = ???
  }

  class OnePossibleBarImpl extends BarInterface[OnePossibleBarImpl] {
//    override def modifyMe(x: Int): OnePossibleBarImpl = ???
  }

  trait UsesBarParametrically[A <: BarInterface[A]] {
    def doSomething(a: A): A = {
      a.modifyMe(2)
    }
  }

  // some other experiments - what if you didnt type T to the immediate descendant?

  class BarImplRefersToPeer extends BarInterface[OnePossibleBarImpl] // works

  // what about specifying a parent?
  class BarPassesThroughTypeParam[T <: BarInterface[T]] extends BarInterface[T]
  // doesn't work - req's an infinite recursion of type params
  //class BarImplRefersToParent extends BarPassesThroughTypeParam[BarPassesThroughTypeParam[BarPassesThroughTypeParam[BarPassesThroughTypeParam]]]
  // does work - specifying self
  class BarImplRefersToSelf extends BarPassesThroughTypeParam[BarImplRefersToSelf]


  // another solution (using type member)
  //-------------------------------------

  trait BazInterface {
    type T <: BazInterface
    def modifyMe(x: Int): T = ???
  }

  class OnePossibleBazImpl extends BazInterface {
    type T = OnePossibleBazImpl
  }

  // this doesnt compile - im not sure how you mix a type param and type member
//  trait UsesBazParametrically[A <: BazInterface] {
//    def doSomething(a: A): A = {
//      a.modifyMe(2)
//    }
//  }

  // nor this - there's no relationship between BazInterface#T and this trait#T
//  trait UsesBazParametrically {
//    type T <: BazInterface
//    def doSomething(a: T): T = {
//      a.modifyMe(2)
//    }
//  }

  // examples using multiple type params on the interface
  // ----------------------------------------------------

  trait QuuxInterface[A, B <: QuuxInterface[A, B]] {
    def modifyMe(x: Int): B = ???
    def createInner: A = ???
  }
  
  class QuuxConcreteImpl extends QuuxInterface[String, QuuxConcreteImpl]

  trait QuuxConcreteConsumer[T, U <: QuuxInterface[T, U]] {
    def doSomething(u: U): U = {
      u.modifyMe(2)
    }
  }

  trait QuuxParametricImpl[A] extends QuuxInterface[A, QuuxParametricImpl[A]]

  trait QuuxParametricConsumer[T, U <: QuuxInterface[T, U]] {
    def doSomething(u: U): U = {
      u.modifyMe(2)
    }
  }

  trait QuuxParametricImplConsumer[T, U <: QuuxParametricImpl[T]] {

    // doesn't compile - by binding to a concrete-ish class (...Impl) we've lost the self-type-param
//    def doSomething(u: U): U = {
//      u.modifyMe(2)
//    }

    def doSomethingElse(u: U): T = {
      u.createInner
    }
  }


  // in the above, in the interface, we wanted to refer to ourselves as a concrete type
  // ie. there is a type param (A), that is related, but I want to refer to my derived self as a concrete type

  // what if the derived type needs to be a type constructor?
  //--------------------------------------------------------

  trait WibbleInterface[A, T <: WibbleInterface[A, T]] {
    def modifyMe(x: Int): T = ???
    def createInner: A = ???
    def createInnerFromMe(t: T): A = t.createInner // wont compile if T is constrained only by T <: Wibble[_, T]
  }

  trait WobbleInterface[A, T[X] <: WobbleInterface[X, T]] {
    def modifyMe(x: Int): T[A]
    def createInner: A = ???
    def foo[B](t: T[A]): T[B] = ???
    def createInnerFromB[B](b: T[B]): B = b.createInner
    def doSomething[B](t: T[A]): T[B] = t.foo(t)
  }

  trait WobbleImpl[A] extends WobbleInterface[A, WobbleImpl]

  trait WobbleConsumer[T[X] <: WobbleInterface[X, T]] {
    def doSomething[A](t: T[A]) = {
      t.modifyMe(2)
    }
  }

}
