package lenses

import monocle._
import org.scalatest.{Matchers, FunSuite}
import scalaz.{\/-, -\/, \/}
import scalaz.syntax.either._


class MonocleLensTest extends FunSuite with Matchers {

  case class Address(street: String, number: Int)

  test("monomorphic lens - modify the number of an Address") {

    val lens = Lens[Address, Int](_.number)(num => addr => addr.copy(number = num))
    lens.modify(_ + 1)(Address("elm street", 13)) should be(Address("elm street", 14))
  }

  case class SuperStreetNumber(number: Int)
  case class SuperAddress(street: String, number: SuperStreetNumber)

  test("polymorphic lens - modify the number of an Address, while changing Address to the more rich SuperAddress") {
    val lens = PLens[Address, SuperAddress, Int, SuperStreetNumber](_.number)(newNumber => { case Address(street, _) => SuperAddress(street, newNumber) })
    lens.modify(num => SuperStreetNumber(num + 1))(Address("elm st", 13)) should be(SuperAddress("elm st", SuperStreetNumber(14)))
  }

  test("Prism operations") {
    import monocle.std.option._
    import monocle.std.disjunction._

    // getOption reflects the fact that the value might not exist in a sum type
    right[String, String, String].getOption(-\/("not here")) should be(None)

    // modify and set both act on the value only if it exists
    some[String, String].modify(s => s.toUpperCase)(Some("foo")) should be(Some("FOO"))
    some[Int, Int].set(4)(None) should be(None)

    // modifyOption and setOption give feedback as to whether they acted on the value
    right[String, Int, Int].modifyOption(_ + 1)(-\/("")) should be(None)
    right[String, Int, Int].setOption(1)(\/-(2)) should be(Some(1.right))
  }

  test("Iso operations") {

    // the basic ops - get and set - are just like Lens
//    PIso[Address, SuperAddress, Int, SuperStreetNumber]{ case Address(_, num) => num }{  }

  }

}
