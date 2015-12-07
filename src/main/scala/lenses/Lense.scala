package lenses

//----------------------------------------------------------------------------
// step zero - domain objects
case class Person(firstName: String, lastName: String, address: Address)
case class Address(city: String, street: String, postcode: Postcode)
case class Postcode(value: String, format: PostcodeFormat.Value)
object PostcodeFormat extends Enumeration { val US, UK = Value }


//----------------------------------------------------------------------------
// step 1 - no common interface
class PostcodeValueLense1 {

  def get(postcode: Postcode): String = postcode.value

  def set(postcode: Postcode, newValue: String): Postcode = postcode.copy(value = newValue)
}

class AddressPostcodeLense1 {

  def get(address: Address): Postcode = address.postcode

  def set(address: Address, postcode: Postcode): Address = address.copy(postcode = postcode)
}

class PersonAddressLense1 {

  def get(person: Person): Address = person.address

  def set(person: Person, address: Address): Person = person.copy(address = address)
}

//----------------------------------------------------------------------------
// step 2 - building bigger lenses while using hardcoded dependencies
//  * moves boilerplate to a class that can be used in many call sites, but is tied very
//    tightly to the path you're trying to navigate

class AddressValueLense {

  // too naeve
  /*
  def get(address: Address): String = address.postcode.value

  def set(address: Address, newValue: String): Address = address.copy(
    postcode = address.postcode.copy(
      value = newValue))
  */

  val addressPostcodeLense = new AddressPostcodeLense1
  def get(address: Address): String = address.postcode.value

  def set(address: Address, newValue: String): Address = addressPostcodeLense.set(
    address, addressPostcodeLense.get(address).copy(value = newValue))
}

//----------------------------------------------------------------------------
// step 3 - making the steps in the path more adjustable

trait Lense3[A, B] {
  def get(a: A): B
  def set(a: A, b: B): A
}

class AddressPostcodeLense3 { self =>

  def get(address: Address): Postcode = address.postcode

  def set(address: Address, postcode: Postcode): Address = address.copy(postcode = postcode)

  def andThen(nextLense: Lense3[Postcode, String]): Lense3[Address, String] = new Lense3[Address, String] {
    def get(address: Address): String = nextLense.get(self.get(address))

    def set(address: Address, newValue: String): Address = {
      self.set(address, nextLense.set(self.get(address), newValue))
    }
  }
}

class PostcodeValueLense3 extends Lense3[Postcode, String] {

  def get(postcode: Postcode): String = postcode.value

  def set(postcode: Postcode, newValue: String): Postcode = postcode.copy(value = newValue)
}


//----------------------------------------------------------------------------
// step 4 - generalise all lenses and move the boilerplate combinator code to trait

trait Lense4[A, B] { self =>
  def get(a: A): B
  def set(a: A, b: B): A
  def andThen[C](nextLense: Lense4[B, C]): Lense4[A, C] = new Lense4[A, C] {
    def get(a: A): C = nextLense.get(self.get(a))

    def set(a: A, newValue: C): A = self.set(a, nextLense.set(self.get(a), newValue))
  }
}

class PostcodeValueLense4 extends Lense4[Postcode, String] {

  def get(postcode: Postcode): String = postcode.value

  def set(postcode: Postcode, newValue: String): Postcode = postcode.copy(value = newValue)
}

class AddressPostcodeLense4 extends Lense4[Address, Postcode] {

  def get(address: Address): Postcode = address.postcode

  def set(address: Address, postcode: Postcode): Address = address.copy(postcode = postcode)
}

class PersonAddressLense4 extends Lense4[Person, Address] {

  def get(person: Person): Address = person.address

  def set(person: Person, address: Address): Person = person.copy(address = address)
}

// more lenses
class MapKeyLense[V](key: String) extends Lense4[Map[String, V], V] {

  def get(map: Map[String, V]): V = map(key)

  def set(map: Map[String, V], newValue: V): Map[String, V] = map.updated(key, newValue)
}

class SeqIndexLense[T](index: Int) extends Lense4[Seq[T], T] {

  def get(seq: Seq[T]): T = seq(index)

  def set(seq: Seq[T], newValue: T): Seq[T] = seq.updated(index, newValue)
}
