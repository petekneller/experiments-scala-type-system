package FboundedTypes

import java.awt.Color

// code examples from:
// http://tpolecat.github.io/2015/04/29/f-bounds.html
object RobNorris1 { // using F-bounded poly

  trait Pet[A <: Pet[A]] { this: A => // self-type
    def name: String
    def renamed(newName: String): A
  }

  case class Fish(name: String, age: Int) extends Pet[Fish] { // note the type argument
    def renamed(newName: String) = copy(name = newName)
  }

  // with the self-type on Pet the following is illegal
//  case class Kitty(name: String, color: Color) extends Pet[Fish] {
//    def renamed(newName: String): Fish = new Fish(newName, 42)
//  }

  // but can still be fudged like so
  class Mammal(val name: String) extends Pet[Mammal] {
    def renamed(newName: String) = new Mammal(newName)
  }

  class Monkey(name: String) extends Mammal(name) // hmm, Monkey is a Pet[Mammal]
}

object RobNorris2 { // using a typeclass

  trait Pet {
    def name: String
  }

  trait Rename[A] {
    def renamed(a: A, newName: String): A
  }

  case class Fish(name: String, age: Int) extends Pet

  implicit class RenameOps[A](a: A)(implicit ev: Rename[A]) {
    def renamed(newName: String) = ev.renamed(a, newName)
  }

  object Fish {
    implicit val FishRename = new Rename[Fish] {
      def renamed(a: Fish, newName: String) = a.copy(name = newName)
    }
  }

  def esquire[A <: Pet : Rename](a: A): A = a.renamed(a.name + ", Esq.")
}

object RobNorris3 { // using a typeclass and removing the supertype

  trait Pet[A] {
    def name(a: A): String
    def renamed(a: A, newName: String): A
  }

  implicit class PetOps[A](a: A)(implicit ev: Pet[A]) {
    def name = ev.name(a)
    def renamed(newName: String): A = ev.renamed(a, newName)
  }

  case class Fish(name: String, age: Int)

  object Fish {
    implicit val FishPet = new Pet[Fish] {
      def name(a: Fish) = a.name
      def renamed(a: Fish, newName: String) = a.copy(name = newName)
    }
  }
}

object RobNorris4 { // het list with f-bounded poly

  import java.awt.Color

  trait Pet[A <: Pet[A]] { this: A =>
    def name: String
    def renamed(newName: String): A
  }

  case class Fish(name: String, age: Int) extends Pet[Fish] {
    def renamed(newName: String) = copy(name = newName)
  }

  case class Kitty(name: String, color: Color) extends Pet[Kitty] {
    def renamed(newName: String) = copy(name = newName)
  }

  def esquire[A <: Pet[A]](a: A): A = a.renamed(a.name + ", Esq.")

  val bob  = Fish("Bob", 12)
  val thor = Kitty("Thor", Color.ORANGE)

  val result = List[A forSome { type A <: Pet[A] }](bob, thor).map(esquire(_))
}

object RobNorris5 { // het list with typeclasses

  import java.awt.Color

  trait Pet[A] {
    def name(a: A): String
    def renamed(a: A, newName: String): A
  }

  implicit class PetOps[A](a: A)(implicit ev: Pet[A]) {
    def name = ev.name(a)
    def renamed(newName: String): A = ev.renamed(a, newName)
  }

  case class Fish(name: String, age: Int)

  object Fish {
    implicit object FishPet extends Pet[Fish] {
      def name(a: Fish) = a.name
      def renamed(a: Fish, newName: String) = a.copy(name = newName)
    }
  }

  case class Kitty(name: String, color: Color)

  object Kitty {
    implicit object KittyPet extends Pet[Kitty] {
      def name(a: Kitty) = a.name
      def renamed(a: Kitty, newName: String) = a.copy(name = newName)
    }
  }

  def esquire[A: Pet](a: A): A = a.renamed(a.name + ", Esq.")

  val bob  = Fish("Bob", 12)
  val thor = Kitty("Thor", Color.ORANGE)

  val pets = List[(A, Pet[A]) forSome { type A }]((bob, implicitly[Pet[Fish]]), (thor, implicitly[Pet[Kitty]]))

  // the below doesn't compile, but the proper incantation follows
//  val result = pets.map(p => esquire(p._1)(p._2))
  val result = pets.map { case (a, pa)  => esquire(a)(pa) }


  // WTF!!!
  trait ∃[F[_]] {
    type A
    val a: A
    val fa: F[A]
    override def toString = a.toString
  }

  object ∃ {
    def apply[F[_], A0](a0: A0)(implicit ev: F[A0]): ∃[F] =
      new ∃[F] {
        type A = A0
        val a = a0
        val fa = ev
      }
  }

  val result2 = List[∃[Pet]](∃(bob), ∃(thor)).map(e => ∃(esquire(e.a)(e.fa))(e.fa))

  // Wow!
  import shapeless._

  object polyEsq extends Poly1 {
    implicit def default[A: Pet] = at[A](esquire(_))
  }

  val result3 = (bob :: thor :: HNil) map polyEsq
}