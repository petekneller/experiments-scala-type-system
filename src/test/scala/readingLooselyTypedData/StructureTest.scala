package pk

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class StructureTest extends FunSuite with ShouldMatchers {

  /*
      Here for posterity, see https://github.com/Poita79/keywords
   */

  val name = Keyword[String]("name")
  val costCentre = Keyword[Integer]("costCentre") // needs to be java.lang.Integer otherwise won't pass the test
  val department = Structure("department",
    name,
    costCentre
  )

  val age = Keyword[Integer]("age")
  val employee = Structure("employee",
    name,
    age,
    department
  )

  test("valid data") {

    val joe: Map[String, Any] = Map(
      "name" -> "Joe Bloggs",
      "age" -> 23,
      "department" -> Map(
        "name" -> "HR",
        "costCentre" -> 1
      )
    )

    employee.conforms(joe) should be(true)
    name(joe) should be("Joe Bloggs")
  }

  test("age fails due to wrong type") {

    val joe: Map[String, Any] = Map(
      "name" -> "Joe Bloggs",
      "age" -> "NaN",
      "department" -> "HR"
    )

    employee.conforms(joe) should be(false)
    age.get(joe) should be(None)
  }

  test("nested structure conforms") {

    val hr: Map[String, Any] = Map(
      "name" -> "HR",
      "costCentre" -> 1
    )

    val joe: Map[String, Any] = Map(
      "department" -> hr
    )

    department.get(joe) should be(Some(hr))
    department(joe) should be(hr)
  }

  test("get can be used in for-comprehensions") {

    val hr: Map[String, Any] = Map(
      "name" -> "HR",
      "costCentre" -> 1
    )

    val joe: Map[String, Any] = Map(
      "department" -> hr
    )

    val cc = for {
      dep <- department.get(joe)
      cc <- costCentre.get(dep)
    } yield cc

    cc should be(Some(1))
  }

  test("can compose functions") {

    val hr: Map[String, Any] = Map(
      "name" -> "HR",
      "costCentre" -> 1
    )

    val joe: Map[String, Any] = Map(
      "department" -> hr
    )

    department(joe) should be(hr)
    val fCostCentre = department andThen costCentre
    fCostCentre(joe) should be(1)
  }

}

