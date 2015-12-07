package pk

// strips out 'empty' entries - Nones, empty strings, empty collections...
object UnboxedMap {
  def apply[A, B](elems: (A, Any)*): Map[A, B] = {
    elems.foldLeft(Map[A, B]())((map, p) => p match {
      case (name, values: Traversable[_]) if values.isEmpty => map
      case (name, values: Seq[B]@unchecked) if values.size == 1 => map + (name -> values.head)
      case (name, values: Set[B]@unchecked) if values.size == 1 => map + (name -> values.head)
      case (name, Some(value: B@unchecked)) => map + p.copy(_2 = value)
      case (name, None) => map
      case (name, "") => map
      case (name, value: B@unchecked) => map + (name -> value)
    })
  }
}