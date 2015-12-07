package pk

import scala._
import net.liftweb.json._

object AsCollectionsOfAnys {

  def employees(data: String): List[Any] = ToSimpleTypes.toMap(data).getOrElse("employees", Map.empty).asInstanceOf[List[Any]]

}



object ToSimpleTypes {
  implicit val formats = DefaultFormats

  def toJson(data: Any): String = {
    writePretty(data.asInstanceOf[AnyRef])
  }

  def toMap[V](value: String): Map[String, V] = {
    toScalaType[Map[String, V]](value)
  }

  def toList[V](value: String): List[V] = {
    toScalaType[List[V]](value)
  }

  def toScalaType[V](value: String) = {
    parse(value).values.asInstanceOf[V]
  }

  def writePretty[A <: AnyRef](a: A): String = {
    Serialization.writePretty(a)
  }

  def prettify(ugly: String): String = {
    writePretty(JsonParser.parse(ugly))
  }
}

