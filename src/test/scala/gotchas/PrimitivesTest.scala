package gotchas

import org.scalatest.FunSuite

class PrimitivesTest extends FunSuite {

  test("some proofs about scala v java primitives") {
    val scala2: scala.Int = 2
    val java2: java.lang.Integer = 2

    // the int types are equivalent
    assert(java2 === scala2)


    // but unfortunately the classes are not considered assignable,
    // i guess because Int and Integer don't share a hierarchy
    //  assert(classOf[java.lang.Integer].isAssignableFrom(classOf[scala.Int])) // fails
    //  assert(classOf[scala.Int].isAssignableFrom(classOf[java.lang.Integer])) // fails


    // compile error:  isInstanceOf cannot test if value types are references
    //    assert(scala2.isInstanceOf[java.lang.Integer])
    // runtime error
    //    assert(scala2.getClass.isInstance(java2))

    assert(java2.isInstanceOf[scala.Int])
    assert(java2.getClass.isInstance(scala2))


    assert(java2.asInstanceOf[scala.Int] === scala2)
    assert(java2.asInstanceOf[scala.Int] === java2)
    assert(scala2.asInstanceOf[java.lang.Integer] === java2)
    assert(scala2.asInstanceOf[java.lang.Integer] === scala2)

  }

}
