package lenses

import org.scalatest.FunSuite

class LenseTest extends FunSuite {

  val wrongPostcodeValue = "EC2M XYZ"
  val correctPostcodeValue = "EC2M ABC"

  val wrongPostcode = Postcode(wrongPostcodeValue, PostcodeFormat.UK)
  val wrongAddress = Address("London", "Old Street", wrongPostcode)
  val joeWithWrongPostcode = Person("Joe", "Bloggs", wrongAddress)

  val correctPostcode = Postcode(correctPostcodeValue, PostcodeFormat.UK)
  val correctAddress = Address("London", "Old Street", correctPostcode)
  val joeWithCorrectPostcode = Person("Joe", "Bloggs", correctAddress)


  test("step zero - the old fashioned way") {
    val joeCorrected = joeWithWrongPostcode.copy(
      address = joeWithWrongPostcode.address.copy(
        postcode = joeWithWrongPostcode.address.postcode.copy(value = "EC2M ABC")))

    assert(joeCorrected === joeWithCorrectPostcode)
  }

  test("step 1 - the postcode -> value lense") {

    val postcodeValueLense = new PostcodeValueLense1

    assert(postcodeValueLense.get(wrongPostcode) === wrongPostcodeValue)

    val postcode2 = postcodeValueLense.set(wrongPostcode, correctPostcodeValue)
    assert(postcode2 === correctPostcode)
  }

  test("step 1 - the address -> postcode lense") {

    val addressPostcodeLense = new AddressPostcodeLense1

    assert(addressPostcodeLense.get(wrongAddress) === wrongPostcode)

    val address2 = addressPostcodeLense.set(wrongAddress, correctPostcode)
    assert(address2 === correctAddress)
  }

  test("step 1 - the person -> address lense") {

    val personAddressLense = new PersonAddressLense1

    assert(personAddressLense.get(joeWithWrongPostcode) === wrongAddress)

    val person2 = personAddressLense.set(joeWithWrongPostcode, correctAddress)
    assert(person2 === joeWithCorrectPostcode)
  }

  test("step 1 - the address -> postcode and postcode -> value lense used together naevely") {

    val addressPostcodeLense = new AddressPostcodeLense1
    val postcodeValueLense = new PostcodeValueLense1

    assert(postcodeValueLense.get(addressPostcodeLense.get(wrongAddress)) === wrongPostcodeValue)
    
    val postcode2 = postcodeValueLense.set(addressPostcodeLense.get(wrongAddress), correctPostcodeValue)
    val address2 = addressPostcodeLense.set(wrongAddress, postcode2)
    assert(address2 === correctAddress)
  }

  test("step 2 - the address -> value uber lense") {

    val addressValueLense = new AddressValueLense

    assert(addressValueLense.get(wrongAddress) === wrongPostcodeValue)

    val address2 = addressValueLense.set(wrongAddress, correctPostcodeValue)
    assert(address2 === correctAddress)
  }

  test("step 3 - making the address -> ? lense more flexible") {

    val addressPostcodeLense = new AddressPostcodeLense3
    val postcodeValueLense = new PostcodeValueLense3
    val addressValueLense = addressPostcodeLense.andThen(postcodeValueLense)

    assert(addressValueLense.get(wrongAddress) === wrongPostcodeValue)

    val address2 = addressValueLense.set(wrongAddress, correctPostcodeValue)
    assert(address2 === correctAddress)
  }

  test("step 4 - generalising to the person -> value lense") {

    val personValueLense = new PersonAddressLense4() andThen
      new AddressPostcodeLense4() andThen
      new PostcodeValueLense4()

    assert(personValueLense.get(joeWithWrongPostcode) === wrongPostcodeValue)

    val person2 = personValueLense.set(joeWithWrongPostcode, correctPostcodeValue)
    assert(person2 === joeWithCorrectPostcode)
  }

  test("step 5 - generalising to maps and seqs") {

    val fooLense = new MapKeyLense[String]("foo")
    val testMap = Map("foo" -> "bar")
    assert(fooLense.get(testMap) === "bar")
    val testMap2 = fooLense.set(testMap, "hello world")
    assert(testMap2 === Map("foo" -> "hello world"))

    val onethLense = new SeqIndexLense[String](1)
    val testSeq = Seq("foo", "bar")
    assert(onethLense.get(testSeq) === "bar")
    val testSeq2 = onethLense.set(testSeq, "hello world")
    assert(testSeq2 === Seq("foo", "hello world"))

  }

  test("step 5 - the whole shebang") {

    val testData = Map(
      "people" -> Seq(joeWithWrongPostcode)
    )

    val personValueLense = new PersonAddressLense4() andThen
      new AddressPostcodeLense4() andThen
      new PostcodeValueLense4()

    val peopleLense = new MapKeyLense[Seq[Person]]("people")
    val zerothLense = new SeqIndexLense[Person](0)

    val theWholeShebangLense = peopleLense andThen zerothLense andThen personValueLense

    assert(theWholeShebangLense.get(testData) === wrongPostcodeValue)

    val testData2 = theWholeShebangLense.set(testData, correctPostcodeValue)
    assert(testData2 === Map("people" -> Seq(joeWithCorrectPostcode)))

  }


}
