package pk

import scala.unchecked

object MapDenormalizer {

  def denormalizeAlongPath(data: Map[String, Any], pathKey: String): Iterable[Map[String, Any]] = {
    for {
      path <- data.get(pathKey).toSeq
      nonPathElements = data.filterKeys(_ != pathKey)
      child <- path match {
        case m: Map[String, Any]@unchecked => Seq(m)
        case it: Iterable[Map[String, Any]]@unchecked => it
        case _ => Seq.empty
      }
    } yield {
      nonPathElements.foldLeft(child){(newChild, e) => newChild + e}
    }
  }

  def denormalizeAll(data: Map[String, Any], newParentKey: String): Iterable[Map[String, Any]] = {
    data.map{ case (key, value) =>
      value match {
        case m: Map[String, Any]@unchecked => m + (newParentKey -> key)
        case _ => Map(newParentKey -> key, key -> value)
      }
    }
  }
}
