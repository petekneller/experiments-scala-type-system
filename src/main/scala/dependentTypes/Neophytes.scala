package dependentTypes

// Taken from: http://danielwestheide.com/blog/2013/02/13/the-neophytes-guide-to-scala-part-13-path-dependent-types.html

object FanFiction {

  class Franchise(name: String) {
    case class Character(name: String)
    def createFanFictionWith(
      lovestruck: Character,
      objectOfDesire: Character): (Character, Character) = (lovestruck, objectOfDesire)
  }

  val starTrek = new Franchise("Star Trek")
  val starWars = new Franchise("Star Wars")

  val quark = starTrek.Character("Quark")
  val jadzia = starTrek.Character("Jadzia Dax")

  val luke = starWars.Character("Luke Skywalker")
  val yoda = starWars.Character("Yoda")

  starTrek.createFanFictionWith(lovestruck = quark, objectOfDesire = jadzia)
  starWars.createFanFictionWith(lovestruck = luke, objectOfDesire = yoda)

  // starTrek.createFanFictionWith(lovestruck = jadzia, objectOfDesire = luke) // Success - doesn't compile

  def createFanFiction(f: Franchise)(lovestruck: f.Character, objectOfDesire: f.Character) =
    (lovestruck, objectOfDesire)

  createFanFiction(starTrek)(lovestruck = quark, objectOfDesire = jadzia)
  createFanFiction(starWars)(lovestruck = luke, objectOfDesire = yoda)

  // createFanFictionWith(?)(lovestruck = jadzia, objectOfDesire = luke) // Success - doesn't work
}

object AwesomeDB {

  object AwesomeDB {
    abstract class Key(name: String) {
      type Value
    }
  }
  import AwesomeDB.Key
  class AwesomeDB {
    import collection.mutable.Map
    val data = Map.empty[Key, Any]
    def get(key: Key): Option[key.Value] = data.get(key).asInstanceOf[Option[key.Value]]
    def set(key: Key)(value: key.Value): Unit = data.update(key, value)
  }

  trait IntValued extends Key {
    type Value = Int
  }

  trait StringValued extends Key {
    type Value = String
  }

  object Keys {
    val foo = new Key("foo") with IntValued
    val bar = new Key("bar") with StringValued
  }
  val dataStore = new AwesomeDB
  dataStore.set(Keys.foo)(23)
  val i: Option[Int] = dataStore.get(Keys.foo)
  //dataStore.set(Keys.foo)("23") // does not compile

}
