/*
Code from James Iry's blog post http://james-iry.blogspot.co.uk/2010/10/phantom-types-in-haskell-and-scala.html
on Phantom Types in Scala and Haskell. All comments below are James's
 */

/*
Some literate Haskell and Scala to demonstrate 
1) phantom types
2) I am a masochist
3) ???
4) profit!

The code is probably best viewed with a syntax colorizer
for one language or the other but I've colorized all my
 comments.

> {-# LANGUAGE EmptyDataDecls #-}
> module RocketModule(test1, test2, createRocket, addFuel, addO2, launch) where

*/
object RocketModule {

  /*
  None of these data types have constructors, so there are
  no values with these types. That's okay because I only
  need the types at compile time. Hence "phantom" -
  ethereal and unreal.

  > data NoFuel
  > data Fueled
  > data NoO2
  > data HasO2

  */
  sealed trait NoFuel
  sealed trait Fueled
  sealed trait NoO2
  sealed trait HasO2

  /*
  The Rocket data type takes two type parameters, fuel and
  o2.  But the constructor doesn't touch them.  I don't
  export the constructor so only this module can create
  rockets.

  > data Rocket fuel o2 = Rocket

  */
  final case class Rocket[Fuel, O2] private[RocketModule]()

  /*
  createRocket produces a rocket with no fuel and no o2

  > createRocket :: Rocket NoFuel NoO2
  > createRocket = Rocket

  */
  def createRocket = Rocket[NoFuel, NoO2]()

  /*
  addFuel takes a rocket with no fuel and returns one with
  fuel.  It doesn't touch the o2

  > addFuel :: Rocket NoFuel o2 -> Rocket Fueled o2
  > addFuel x = Rocket

  */
  def addFuel[O2](x : Rocket[NoFuel, O2]) = Rocket[Fueled, O2]()

  /*
  Similarly, addO2 adds o2 without touching the fuel

  > addO2 :: Rocket fuel NoO2 -> Rocket fuel HasO2
  > addO2 x = Rocket

  */
  def addO2[Fuel](x : Rocket[Fuel, NoO2]) = Rocket[Fuel, HasO2]()

  /*
  launch will only launch a rocket with both fuel and o2

  > launch :: Rocket Fueled HasO2 -> String
  > launch x = "blastoff"

  */
  def launch(x : Rocket[Fueled, HasO2]) = "blastoff"

  /*
  This is just a pretty way of stringing things together,
  stolen shamelessly from F#.  Adding infix operations is
  a bit verbose in Scala.

  > a |> b = b a

  */
  implicit def toPiped[V] (value:V) = new {
    def |>[R] (f : V => R) = f(value)
  }

  /*
  Create a rocket, fuel it, add o2, then
  launch it

  > test1 = createRocket |> addFuel |> addO2 |> launch

  */
  def test1 = createRocket |> addFuel |> addO2 |> launch

  /*
  This compiles just fine, too.  It doesn't matter which
  order we put in the fuel and o2

  > test2 = createRocket |> addO2 |> addFuel |> launch

  */
  def test2 = createRocket |> addO2 |> addFuel |> launch

  //This won't compile - there's no fuel

  // > test3 = createRocket |> addO2 |> launch

  //    def test3 = createRocket |> addO2 |> launch

  // This won't compile either - there's no o2

  // > test4 = createRocket |> addFuel |> launch

  //    def test4 = createRocket |> addFuel |> launch

  // This won't compile because you can't add fuel twice

  // > test5 = createRocket |> addFuel |> addO2 |> addFuel |> launch

  //    def test5 = createRocket |> addFuel |> addO2 |> addFuel |> launch
}