package pk

import org.scalatest.FunSuite
import net.liftweb.json._
import org.scalatest.matchers.ShouldMatchers

class AsJObjectsTest extends FunSuite with ShouldMatchers {

  val jobjects = parse(Common.employeesExample)

  test("employees example") {

    def withName(name: String): JValue => Boolean = { v: JValue =>
      AsJObjects.getField[String](v, "name").map(_ == "Joe Bloggs").getOrElse(false)
    }

    val joesAge = for {
      employees <- AsJObjects.section(jobjects, "employees")
      joe <- AsJObjects.find(employees, withName("Joe Bloggs"))
      joesAge <- AsJObjects.getField[BigInt](joe, "age")
    } yield joesAge

    joesAge should be(Some(23))

  }

}
