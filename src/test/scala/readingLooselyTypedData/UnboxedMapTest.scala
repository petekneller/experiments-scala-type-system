package pk

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class UnboxedMapTest extends FunSuite with ShouldMatchers {

  test("Nones are removed from the structure") {

    val joe = UnboxedMap(
      "name" -> "Joe",
      "age" -> None
    )

    joe.get("age") should be(None)
  }

}
