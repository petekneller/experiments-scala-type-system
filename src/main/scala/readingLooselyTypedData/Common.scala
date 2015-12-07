package pk

object Common {

  val employeesExample: String =
    """
      |  {
      |    "employees": [
      |      {
      |        "name": "Joe Bloggs"
      |        "age": 23
      |        "department": "HR"
      |      },
      |      {
      |        "name": "Sally Shears"
      |        "age": 38
      |        "department": "Accounting"
      |      }
      |    ]
      |    "departments": [
      |      {
      |        "name": "HR"
      |        "numStaff": 9
      |      },
      |      {
      |        "name": "Accounting"
      |        "numStaff": 3
      |      }
      |    ]
      |  }
    """.stripMargin



}
