package gotchas

object WhenAFunctionIsNotAFunction {

  class A { def apply(i: Int) = i } // does NOT extend FunctionX or =>

  // but can be treated as a function using the apply syntactic sugar
  val a = new A
  val r1 = a(3)


  // these ARE true functions
  class B extends Function1[Int, Int] { def apply(i: Int) = i }
  class C extends (Int => Int) { def apply(i: Int) = i }

  val b = new B
  val r2 = b(3)
  val c = new C
  val r3 = c(3)


  // both styles can be treated as functions when applied, but not when used as args to
  // a fn that requires a fn type
  def takesAFn[T](f: T => T, x: T): T = f(x)

  // these are fine, as both b and c are a subtype of fn
  val r4 = takesAFn(b, 3)
  val r5 = takesAFn(c, 3)
  // but a is not
  //val r5 = takesAFn(a, 3) // so this doesn't compile

}
