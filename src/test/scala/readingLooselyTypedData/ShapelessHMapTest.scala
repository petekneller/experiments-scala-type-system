package pk

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import shapeless._
import syntax.singleton._
import record._
import shapeless.ops.record.Selector
import syntax.typeable._

class ShapelessHMapTest extends FunSuite with ShouldMatchers {

  test("valid data") {

    val joe =
        ("name" ->> "Joe Bloggs") ::
        ("age" ->> 23) ::
        ("department" ->> "HR") ::
        HNil

    val joesDetails = "Joe Bloggs" :: 23 :: "HR" :: HNil

//    val book =
//      ("author" ->> "Benjamin Pierce") ::
//        ("title"  ->> "Types and Programming Languages") ::
//        ("id"     ->>  262162091) ::
//        ("price"  ->>  44.11) ::
//        HNil

    // won't compile - too many elements
    //val x: Any :: Any :: Any :: Any :: HNil = joesDetails.unify

    // won't compile - types must be uniform after unification
    //val x: Any :: Any :: String :: HNil = joesDetails.unify

    val x: Any :: Any :: Any :: HNil = joesDetails.unify

    // compiles - covariant in element types
    //val x: Any :: Int :: Any :: HNil = joesDetails

    // should work, but doesn't - for some reason the cast from List[Any] to HList isnt working
    //val x: List[Any] = joesDetails.toList

    //val y = x.cast[String :: Int :: String :: HNil]
    val y = x.cast[joesDetails.type]

    y.get.head should be("Joe Bloggs")
    y.get.tail.head should be(23)

  }

}
