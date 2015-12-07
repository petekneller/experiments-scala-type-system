package pk

import net.liftweb.json._

object AsJObjects {

  def section(json: JValue, name: String): Option[JValue] = json match {
    case JObject(fields) => fields.find(_.name == name).map(_.value)
    case _ => None
  }

  def find(json: JValue, p: JValue => Boolean): Option[JValue] = json match {
    case JArray(items) => items.find(p)
    case _ => None
  }

  def getField[A](json: JValue, field: String): Option[A] = json match {
    case JObject(fields) => fields.find(_.name == field).flatMap(toType[A])
    case _ => None
  }

  private def toType[A](field: JField): Option[A] = {
    val untyped: Option[Any] = field.value match {
      case JString(s) => Some(s: Any)
      case JInt(i) => Some(i: Any)
      case JDouble(d) => Some(d: Any)
      case _ => None
    }
    untyped.map(_.asInstanceOf[A])
  }


}
