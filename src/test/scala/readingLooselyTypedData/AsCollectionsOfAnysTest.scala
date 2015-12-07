package pk

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class AsCollectionsOfAnysTest extends FunSuite with ShouldMatchers {

  ignore("employees example") {

    val employees = AsCollectionsOfAnys.employees(Common.employeesExample)
    val joe = employees.find(e => e.asInstanceOf[Map[String, Any]]("name") == "Joe Bloggs").map(_.asInstanceOf[Map[String, Any]]).get
    val joesAge = joe("age").asInstanceOf[Int]

    joesAge should be(23)

  }

}
