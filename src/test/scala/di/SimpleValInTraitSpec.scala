package nl.rintcius.poc

import org.specs2.mutable.Specification

/**
 * @author Rintcius Blok
 */
object SimpleValInTraitSpec {

  trait A {
    val someVal: String
    val aVal = someVal
  }

  trait B {
    val someVal: String
    lazy val aLazyVal = someVal

    def aDef = someVal
  }

  trait C {
    val someVal: String
    println(someVal.substring(0, 3))  //potential null pointer dereference: someVal.substring
  }

}

import SimpleValInTraitSpec._

class SimpleValInTraitSpec extends Specification {

  "A" should {  // potential null pointer dereference: SimpleValInTraitSpec.this.described("A").should
    "A val" in {  // potential null pointer dereference: SimpleValInTraitSpec.this.inExample("A val").in
      new A {
        val someVal = "value"
      }.aVal.substring(0, 3) must throwA[NullPointerException] // potential null pointer dereference: { (3 warnings)
    }

    "A patch" in { // potential null pointer dereference: SimpleValInTraitSpec.this.inExample("A patch").in
      new {
        val someVal = "value"
      } with A {}.aVal.substring(0, 3) must_== "val" // potential null pointer dereference: { (3 warnings)
    }
  }

  "B" should { // potential null pointer dereference: SimpleValInTraitSpec.this.described("B").should
    "B lazyval" in { // potential null pointer dereference: SimpleValInTraitSpec.this.inExample("B lazyval").in
      new B {
        val someVal = "value"
      }.aLazyVal.substring(0, 3) must_== "val" // potential null pointer dereference: { (3 warnings)
    }

    "B def" in { // potential null pointer dereference: SimpleValInTraitSpec.this.inExample("B def").in
      new B {
        val someVal = "value"
      }.aDef.substring(0, 3) must_== "val" // potential null pointer dereference: { (3 warnings)
    }
  }

  "C val" should { // potential null pointer dereference: SimpleValInTraitSpec.this.described("C val").should
    "C instantiate" in { // potential null pointer dereference: SimpleValInTraitSpec.this.inExample("C instantiate").in
      new C {
        val someVal = "value"
      } must throwA[NullPointerException] // potential null pointer dereference: SimpleValInTraitSpec.this.theValue[nl.rintcius.poc.SimpleValInTraitSpec.C]({
    }
  }

}

