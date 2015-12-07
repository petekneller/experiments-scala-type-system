package nl.rintcius.poc

import org.specs2.mutable.Specification

/**
 * @author Rintcius Blok
 */
object AbstractValInTraitSpec {

  /**
   * Example code for this blog post:
   * http://blog.rintcius.nl/post/scala-traits-as-well-defined-modules-and-a-crime-scene-investigation.html
   *
   * **Hypotheses:**
   *
   * 1. It is always ok to declare abstract values in traits. (marked as OK in the code)
   * 2. Avoid code that uses abstract values in traits during the initialization phase (marked as NOT OK in the code).
   * 3. After the trait is initialized it is ok to use abstract values.
   *
   * Furthermore, code that "patches" code which is NOT OK is also marked.
   *
   * Counter arguments & Refinements welcome (with evidence)!
   */

  trait A_TraitWithAbstractVal {
    val someVal: String // OK!

    def aDef = someVal
    lazy val aLazyVal = someVal
    val aVal = someVal // NOT OK!

    println(someVal) //NOT OK!
  }

  trait B_TraitExtendingA extends A_TraitWithAbstractVal {
    override val aVal = someVal // NOT OK!
    override lazy val aLazyVal = someVal
    override def aDef = someVal

    def bDef = someVal
    lazy val bLazyVal = someVal
    val bVal = someVal // NOT OK!
  }

  trait C_TraitRequiringA {
    self: A_TraitWithAbstractVal =>

    def cDef = someVal
    lazy val cLazyVal = someVal
    val cVal = someVal // NOT OK!
  }

  trait D_TraitHavingAAsAbstractVal {
    val aTraitWithVal: A_TraitWithAbstractVal // OK!

    def dDef = aTraitWithVal.someVal
    lazy val dLazyVal = aTraitWithVal.someVal
    val dVil = aTraitWithVal.someVal // NOT OK! (TRAIT D CANNOT EVEN BE INSTANTIATED)
  }

  trait E_TraitHavingAAsAbstractVal {
    val aTraitWithVal: A_TraitWithAbstractVal // OK!

    def eDef = aTraitWithVal.someVal
    lazy val eLazyVal = aTraitWithVal.someVal
    //difference with D: now without the evil/devil
    //val eVil = aTraitWithVal.someVal
  }

  trait F_TraitHavingInstantiatedAAsVal {
    val aTraitWithVal_instantiated = new A_TraitWithAbstractVal {
      val someVal = "OK"
    }
    val fDef = aTraitWithVal_instantiated.someVal
    val fLazyVal = aTraitWithVal_instantiated.someVal
    val fVal = aTraitWithVal_instantiated.someVal
  }

  trait G_TraitHavingInstantiated_EarlyDef_WithAAsVal {
    // Code that "patches" A:
    val aTraitWithValWithEarlyDef = new { val someVal = "OK" } with A_TraitWithAbstractVal {}

    val gDef = aTraitWithValWithEarlyDef.someVal
    val gLazyVal = aTraitWithValWithEarlyDef.someVal
    val gVal = aTraitWithValWithEarlyDef.someVal
  }

  trait H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {
    trait TraitThatDefinesSomeVal {
      val someVal = "OK"
    }
    // Code that "patches" A:
    val aTraitWithValWithEarlyDef = new TraitThatDefinesSomeVal with A_TraitWithAbstractVal {}

    val hDef = aTraitWithValWithEarlyDef.someVal
    val hLazyVal = aTraitWithValWithEarlyDef.someVal
    val hVal = aTraitWithValWithEarlyDef.someVal
  }
}

import AbstractValInTraitSpec._

class AbstractValInTraitSpec extends Specification {

  "A - the trait itself" should {

    "[NOT OK] refer to abstract val from val" in {
      new A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from lazy def" in {
      new A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aDef must_== "OK"
    }
  }

  "B - extends trait" should {

    "[NOT OK] refer to abstract val from val" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.aVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.aLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.aDef must_== "OK"
    }

    "[NOT OK] refer to abstract val from val" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.bVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.bLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new B_TraitExtendingA {
        val someVal = "OK"
      }.bDef must_== "OK"
    }
  }

  "C - requires trait" should {

    "[NOT OK] refer to abstract val from val" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.aDef must_== "OK"
    }

    "[NOT OK] refer to abstract val from val" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.cVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.cLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new C_TraitRequiringA with A_TraitWithAbstractVal {
        val someVal = "OK"
      }.cDef must_== "OK"
    }


  }

  "D - has trait A as abstract val; D has val referring to abstract val" should {

    "[NOT OK] refer to abstract val from val => trait D cannot be instantiated" in {
      new D_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      } must throwA[NullPointerException]
    }
  }

  "E - has trait A as abstract val; E has no val referring to abstract val" should {

    "[OK] refer to abstract val from lazy val" in {
      new E_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      }.eLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new E_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      }.eDef must_== "OK"
    }

    "[NOT OK] refer to abstract val from val" in {
      new E_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      }.aTraitWithVal.aVal must_== null
    }

    "[OK] refer to abstract val from lazy val" in {
      new E_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      }.aTraitWithVal.aLazyVal must_== "OK"
    }

    "[OK] refer to abstract val from def" in {
      new E_TraitHavingAAsAbstractVal {
        val aTraitWithVal = new A_TraitWithAbstractVal {
          val someVal = "OK"
        }
      }.aTraitWithVal.aDef must_== "OK"
    }
  }


  "F - has instantiated A as val" should {

    "[OK] fLazyVal" in {
      new F_TraitHavingInstantiatedAAsVal {}.fLazyVal must_== "OK"
    }

    "[OK] fDef" in {
      new F_TraitHavingInstantiatedAAsVal {}.fDef must_== "OK"
    }

    "[OK] fVal" in {
      new F_TraitHavingInstantiatedAAsVal {}.fVal must_== "OK"
    }

    "[OK] aTraitWithVal_instantiated.aLazyVal" in {
      new F_TraitHavingInstantiatedAAsVal {}.aTraitWithVal_instantiated.aLazyVal must_== "OK"
    }

    "[OK] aTraitWithVal_instantiated.aDef" in {
      new F_TraitHavingInstantiatedAAsVal {}.aTraitWithVal_instantiated.aDef must_== "OK"
    }

    "[NOT OK] aTraitWithVal_instantiated.aVal" in {
      new F_TraitHavingInstantiatedAAsVal {}.aTraitWithVal_instantiated.aVal must_== null
    }

  }

  "G - 'patch' example (G could be considered well-defined, by patching A)" should {

    "[OK] gLazyVal" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.gLazyVal must_== "OK"
    }

    "[OK] gDef" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.gDef must_== "OK"
    }

    "[OK] gVal" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.gVal must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aLazyVal" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.aTraitWithValWithEarlyDef.aLazyVal must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aDef" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.aTraitWithValWithEarlyDef.aDef must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aVal" in {
      new G_TraitHavingInstantiated_EarlyDef_WithAAsVal {}.aTraitWithValWithEarlyDef.aVal must_== "OK"
    }

  }

  "H - 'patch' example (H could be considered well-defined, by patching A)" should {

    "[OK] hLazyVal" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.hLazyVal must_== "OK"
    }

    "[OK] hDef" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.hDef must_== "OK"
    }

    "[OK] hVal" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.hVal must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aLazyVal" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.aTraitWithValWithEarlyDef.aLazyVal must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aDef" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.aTraitWithValWithEarlyDef.aDef must_== "OK"
    }

    "[OK] aTraitWithValWithEarlyDef.aVal" in {
      new H_TraitHavingInstantiated_TraitThatDefinesSomeVal_WithAAsVal {}.aTraitWithValWithEarlyDef.aVal must_== "OK"
    }

  }
}

