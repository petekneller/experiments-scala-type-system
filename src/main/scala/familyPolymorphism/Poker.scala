package familyPolymorphism

// from http://stackoverflow.com/questions/14133748/family-polymorphism-in-scala

object StackOverflow {

  trait Game {
    type Player
    type State <: StateLike

    trait StateLike {
      def player: Player
    }

    def startState: State
  }

  class Poker extends Game {
    class PokerPlayer
    type Player = PokerPlayer

    class PokerState extends StateLike {
      def player = new PokerPlayer
    }
    type State = PokerState

    val startState = new PokerState
  }

}
