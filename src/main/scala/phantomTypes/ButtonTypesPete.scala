package phantomTypes

/*
A port of Channing's Phantom type example to use typeclasses

Didn't work out quite the way I'd hoped. Below is the email I sent Channing explaining my findings.

Hey Channing,

I'm a bit late to the party, but I just found your example of using Phantom Types in Scala. Awesome! Thx. I wouldn't have thought to use covariance to enable that kind of typechecking. I was going to reply to your post, but I guess you've disabled comments, so thought I'd mail you instead.

Your example reminded me of an example on the Haskell wiki (the resistors at http://www.haskell.org/haskellwiki/Smart_constructors#Enforcing_the_constraint_statically) in which they used PTs and typeclasses to do something a little similar. At the moment I'm on a bit of a typeclass bender, mainly because they're so extensible, so I thought I'd port your example to use typeclasses and see how it went. Hmm. Not quite as clean as I'd hoped. Specifically, I feel like I've failed somewhat if I have to refer to an objects .type in a type sig. I've included the code below for your amusement. I was hoping to modify your example to allow tagging/categorizing of the buttons after the fact, assuming you hadnt designed-in the phantom type from the beginning. Apart from being more verbose, I guess it falls down because you've taken advantage of Scala "object"s. Using PTs means you've categorized the subtypes of the Button trait, and with objects in the mix, you've categorized the instances in one fell swoop. Typeclasses, being external, categorise only the subtypes directly, which leads to the slightly clunky "Disabled.type" type sig.

Take what I've just said with a pinch of salt - I'm trying to blend your example into what I already know and to clarify in my mind where each approach suits, so I might have made some rash statements and drawn some mistaken conclusions.


Cheers,
Pete.
*/

object PhantomsPete {

  /* Here is a Button, some implementations and an ActionPanel
   * that uses two Buttons. */
  trait Button

  // PETE: Typeclasses that categorise the buttons
  trait Acceptor[T <: Button]
  trait Rejector[T <: Button]

  // and the usual subclasses
  case object Reject extends Button
  case object Accept extends Button

  // PETE: tag/label/categorise the buttons
  implicit object RejectButtonIsARejector extends Rejector[Reject.type]
  implicit object AcceptButtonIsAnAcceptor extends Acceptor[Accept.type]

  // and panel
  case class ActionPanel3[B1 <: Button : Acceptor, B2 <: Button : Rejector](accept: B1, reject: B2)

  // And now the DisabledButton
  case object Disabled extends Button
  // PETE
  implicit object DisabledIsARejector extends Acceptor[Disabled.type]

  // and in use
  ActionPanel3(Disabled, Reject)
}