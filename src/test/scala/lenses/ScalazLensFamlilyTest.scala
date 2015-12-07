package lenses

import org.scalatest.{Matchers, FunSuite}
import scalaz.LensFamily
import LensFamily._

class ScalazLensFamilyTest extends FunSuite with Matchers {

  case class Address(street: String, number: Int)
  case class Person(name: String, address: Address)
  val streetNumberLens = lensFamilyu((a: Address, num: Int) => a.copy(number = num), (a: Address) => a.number)
  val freddysPlace: Address = Address("elm street", 13)

  test("lensfamilyu") {
    streetNumberLens.set(freddysPlace, 14) should be(Address("elm street", 14))
  }

  case class StreetNumberPlus(number: Int)
  val streetNumberPlusLens = streetNumberLens.xmapB[StreetNumberPlus, StreetNumberPlus]((num: Int) => StreetNumberPlus(num))({ case StreetNumberPlus(num) => num })

  test("xmapB - focus the lens on a better street number") {

    streetNumberPlusLens.set(freddysPlace, StreetNumberPlus(14)) should be(Address("elm street", 14))
  }

  case class AddressPlus(street: String, number: StreetNumberPlus)
  val freddysPlacePlus = AddressPlus("elm street", StreetNumberPlus(13))

  test("xmapA - lens into a better address") {
    val addressPlusLens = streetNumberLens.xmapA[AddressPlus, AddressPlus]({ case Address(street, num) => AddressPlus(street, StreetNumberPlus(num)) })({ case AddressPlus(street, StreetNumberPlus(num)) => Address(street, num) })
    addressPlusLens.set(freddysPlacePlus, 14) should be(AddressPlus("elm street", StreetNumberPlus(14)))
  }

  test("xmapA and xmapB - lens into an old address, focusing on the old street number and converting both to a better version") {

    val lens1 = streetNumberLens.xmapA[Address, AddressPlus]({ case Address(street, num) => AddressPlus(street, StreetNumberPlus(num)) })(identity)
    lens1.set(freddysPlace, 14) should be(AddressPlus("elm street", StreetNumberPlus(14)))

    // Not sure how to do this - I want to be able to set a StreetNumberPlus directly without having to map back to int
//    val lens2 = lens1.xmapB((num: Int) => StreetNumberPlus(num))(identity)
  }

}
