package gotchas

object NoArgMethods {


  val l = List(1, 2, 3)
  val correct = l.toSet // has type Set[Int]
  val wrong = l.toSet() // has type Boolean

  // toSet() is converted to toSet.apply()
  // (no surprise there)
  // what is surprising is that toSet.apply() compiles!
  // apply on toSet is a unary method, what happens when its applied with 0 args?
  // looks like the compiler is creating a call with unit

  val s = l.toSet


  // fully expanded
  // -------------------

  val s1 = l.toSet.apply() // why does this compile?
  //val s2 = s.apply() // fails with 'not enough arguments'

  val s3 = l.toSet apply () // can see that this might be ambiguous; see below
  //val s4 = s apply () // fails with 'not enough arguments'


  // partially expanded
  // -------------------

  val s5 = l.toSet.apply () // can see that this might be ambiguous; see below
  //val s6 = s.apply () // fails with 'not enough arguments'

  //val s7 = l.toSet.apply // fails with 'Missing arguments'
  //val s8 = s.apply // fails with 'Missing arguments'
  // ^ Good. The wouldn't expect the compiler to ever infer a missing arg

  val s9 = l.toSet apply() // why does this compile?
  //val s10 = s apply() // fails with 'not enough arguments'

  //val s11 = l.toSet apply // fails with 'Missing arguments'
  //val s12 = s apply // fails with 'Missing arguments'
  // ^ Good. The wouldn't expect the compiler to ever infer a missing arg


  // The problem never occurs when empty apply call is made against a val/var of the correct type, it only occurs
  // when made against an intermediate object. Why?

  // I can sort of see the ambiguity where you're calling foo(Int) like
  //    foo ()
  // is that meant to be interpreted as:
  //    foo()     (called with no args)
  // or
  //    foo(())   (called with one arg of unit type)


  // After speaking to Dan B, the problem becomes deeper - the inferred type of the intermediate Set is Set[Any],
  // so we're asking the set if unit is a member

  val sa: Set[Any] = l.toSet // remove the type annotation and the below line fails to compile (as the previous example of the same shape)
  val sa1 = sa()


  // And the solution is:
  // ("-Yno-adapted-args", "Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.")

}
