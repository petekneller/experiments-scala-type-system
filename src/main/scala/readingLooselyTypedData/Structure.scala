package pk

  /*
      Here for posterity, see https://github.com/Poita79/keywords
   */

trait Keyword[V] extends (Map[String, Any] => V) { self =>
  val key: String
  val aClass: Class[V]

  def get(candidate: Map[String, Any]): Option[V] = {
    candidate.get(key) flatMap { v =>
      if (aClass.isAssignableFrom(v.getClass)) Some(v.asInstanceOf[V])
      else None
    }
  }

  def apply(candidate: Map[String, Any]): V = get(candidate) getOrElse { throw new NoSuchElementException(key) }

}


case class Structure(key: String, keywords: Keyword[_]*) extends Keyword[Map[String, Any]] {
  val aClass = classOf[Map[String, Any]]

  def conforms(candidate: Map[String, Any]): Boolean = {
    keywords forall { keyword =>
      candidate.contains(keyword.key) &&
        keyword.aClass.isAssignableFrom(candidate(keyword.key).getClass)
    }
  }
}


object Keyword {

  def apply[V: Manifest](name: String) = new Keyword[V] {
    val key = name
    val aClass = implicitly[Manifest[V]].runtimeClass.asInstanceOf[Class[V]]
  }
}


