package pk

import org.scalatest.FunSuite
import org.junit.Assert._
import org.hamcrest.Matchers._

class MapDenormalizerTest extends FunSuite {

  test("child maps gain keys of parent map") {

    val testData = Map[String, Any](
      "parentKeyA" -> "a",
      "parentKeyB" -> "b",
      "child" -> Map(
        "childKeyA" -> "foo",
        "childKeyB" -> 2
      )
    )

    val result = MapDenormalizer.denormalizeAlongPath(testData, "child")
    assertThat[Iterable[Map[String, Any]]](result, is[Iterable[Map[String, Any]]](Some(Map[String, Any](
      "childKeyA" -> "foo",
      "childKeyB" -> 2,
      "parentKeyA" -> "a",
      "parentKeyB" -> "b"
    ))))
  }

  test("collections of children gain keys of parent map") {

    val testData = Map[String, Any](
      "parentKeyA" -> "a",
      "parentKeyB" -> "b",
      "children" -> Seq(
        Map[String, Any](
          "name" -> "child1"
        ),
        Map[String, Any](
          "name" -> "child2"
        )
      )
    )

    val result = MapDenormalizer.denormalizeAlongPath(testData, "children")
    assertThat[Iterable[Map[String, Any]]](result, is[Iterable[Map[String, Any]]](Seq(
      Map(
        "parentKeyA" -> "a",
        "parentKeyB" -> "b",
        "name" -> "child1"
      ),
      Map(
        "parentKeyA" -> "a",
        "parentKeyB" -> "b",
        "name" -> "child2"
      )
    )))
  }

  test("denormalizeAll flattens maps that are nothing but dictionaries of children") {

    val testData = Map[String, Any](
      "foo" -> Map[String, Any](
        "foo" -> 1,
        "bar" -> "hello"
      ),
      "bar" -> Map[String, Any](
        "foo" -> "bar"
      ),
      "baz" -> Map[String, Any](
        "watzit" -> true
      )
    )

    val result = MapDenormalizer.denormalizeAll(testData, "_p")
    assertThat[Iterable[Map[String, Any]]](result, is[Iterable[Map[String, Any]]](Seq(
      Map(
        "_p" -> "foo",
        "foo" -> 1,
        "bar" -> "hello"
      ),
      Map(
        "_p" -> "bar",
        "foo" -> "bar"
      ),
      Map(
        "_p" -> "baz",
        "watzit" -> true
      )
    )))
  }

  test("denormalizeAll leaves turns atoms and seqs into maps containing themselves") {

    val testData = Map[String, Any](
      "foo" -> Map[String, Any](
        "subFoo" -> "hello",
        "subBar" -> 2
      ),
      "bar" -> 3,
      "baz" -> Seq(true, false, true)
    )

    val result = MapDenormalizer.denormalizeAll(testData, "par_")
    assertThat[Iterable[Map[String, Any]]](result, is[Iterable[Map[String, Any]]](Seq(
      Map[String, Any](
        "par_" -> "foo",
        "subFoo" -> "hello",
        "subBar" -> 2
      ),
      Map[String, Any](
        "par_" -> "bar",
        "bar" -> 3
      ),
      Map[String, Any](
        "par_" -> "baz",
        "baz" -> Seq(true, false, true)
      )
    )))
  }

  test("can be run several times") {

    val testData = Map[String, Any](
      "parentKeyA" -> "a",
      "children" -> Seq(
        Map[String, Any](
          "parentKeyB" -> 1,
          "childrensChildren" -> Seq(
            Map[String, Any](
              "childKeyA" -> "foo"
            ),
            Map[String, Any](
              "childKeyA" -> "bar"
            )
          )
        ),
        Map[String, Any](
          "parentKeyB" -> 2,
          "childrensChildren" -> Seq(
            Map[String, Any](
              "childKeyB" -> "foo"
            ),
            Map[String, Any](
              "childKeyB" -> "bar"
            )
          )
        )
      )
    )

    val result = for {
      firstLevelChild <- MapDenormalizer.denormalizeAlongPath(testData, "children")
      secondLevelChild <- MapDenormalizer.denormalizeAlongPath(firstLevelChild, "childrensChildren")
    } yield secondLevelChild


    assertThat[Iterable[Map[String, Any]]](result, is[Iterable[Map[String, Any]]](Seq(
      Map[String, Any](
        "parentKeyA" -> "a",
        "parentKeyB" -> 1,
        "childKeyA" -> "foo"
      ),
      Map[String, Any](
        "parentKeyA" -> "a",
        "parentKeyB" -> 1,
        "childKeyA" -> "bar"
      ),
      Map[String, Any](
        "parentKeyA" -> "a",
        "parentKeyB" -> 2,
        "childKeyB" -> "foo"
      ),
      Map[String, Any](
        "parentKeyA" -> "a",
        "parentKeyB" -> 2,
        "childKeyB" -> "bar"
      )
    )))
  }

}
