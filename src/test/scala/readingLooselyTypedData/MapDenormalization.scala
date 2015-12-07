package pk

import org.scalatest.FunSuite
import net.liftweb.json
import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.DefaultFormats

class MapDenormalizationUsingJsonPath extends FunSuite {
  implicit val formats = DefaultFormats

  val testData = Map(
    "group1" -> Seq(
      Map(
        "foo" -> 1,
        "bar" -> Seq(
          Map(
            "baz" -> "hello",
            "bob" -> "world"
          )
        )
      )
    )
  )

  test("using lift-json XPath") {

    val jValues = json.parse(json.Serialization.write(testData))

  //    val groups = jValues.map {
  //      case JObject()
  //    }

  }

}
