package GADTs

// Pauls' Gist here: https://gist.github.com/pchiusano/1369239

object PaulChiusano {

  /** GADTs in Scala and their limitations */

  /** Background: what is an algebraic data type (ADT) ?
    * ADT: (possibly) recursive datatype with sums and products
    * In scala - a trait with case classes (case class is product, subtyping is sum)
    */

  /** Motivation: untyped embedded DSL doesn't prevent nonsensical expressions */
  sealed trait Expr {
    def apply(other: Expr) = Ap(this, other)
    def eval: Expr = this match {
      case Ap(f,e) => f.eval match {
        case Fn(f) => f(e).eval(e.eval).eval
        case f2    => sys.error("application of non-function")
      }
      case _ => this
    }
  }
  case class S(s: String) extends Expr
  case class N(i: Int) extends Expr
  case class Ap(f: Expr, arg: Expr) extends Expr
  case class Fn(f: Expr => Expr) extends Expr

  object Untyped {

    def main(args: Array[String]): Unit = {
      val x = N(1)
      val factorial = Fn { case N (i) => N ((1 to i).product) }
      val f4 = factorial(x)
      val f5 = factorial(S("hello world!")) // compiles fine!!! :(
      println (f5.eval)
    }
  }

  /** Generalized algebraic data types (GADTs) let you give a different type
    * to each data constructor. Also, pattern matching on each data constructor
    * allows you to recover and appropriately refine type information.
    * (Scala does not fully support this)
    */

  trait Expr2[A] {
    def eval: A
  }
  case class Atom2[A](a: A) extends Expr2[A] {
    def eval = a
  }
  case class Ap2[A,B](f: Expr2[A => B], arg: Expr2[A]) extends Expr2[B] {
    def eval = f.eval(arg.eval)
  }
  case class Fn2[A,B](f: Expr2[A] => Expr2[B]) extends Expr2[A => B] {
    def eval = (a: A) => f(Atom2(a)).eval
  }

  object GADT1 {
    /** This implementation also compiles - suggested by Mark Harrah.
      * Note the use of (existential) type variables in pattern. */
    def eval[A](e: Expr2[A]): A = e match {
      case Atom2(a) => a
      case Ap2(f,a) => eval(f)(eval(a))
      case f: Fn2[a,b] => ((x:a) => eval(f.f(Atom2[a](x))))
    }
    def main(args: Array[String]): Unit = {
      val x = Atom2(4)
      val factorial = Fn2 { (e: Expr2[Int]) => Atom2 ((1 to e.eval).product) }
      val e = Ap2(factorial, x)
      //  val e2 = Ap2(factorial, Atom2("w00t")) // error!!
      println (e.eval)
    }
  }


  /** More problems with deep pattern matching and lack of type refinement in pattern matching */

  /** GADTs for stream transforming functions */
  trait F[A,B] {
    def eval: List[A] => List[B]
    // def optimize: F[A,B] = this
    def pipe[C](f: F[B,C]): F[A,C] = Pipe(this, f)
    def |[C](f: F[B,C]): F[A,C] = this pipe f
  }
  case class MapF[A,B](f: A => B) extends F[A,B] {
    def eval = _ map f
    override def pipe[C](g: F[B,C]) = g match {
      case MapF(g) => MapF(f andThen g)
      /** Commented out lines do not compile, the pattern match does not refine
        *  the type B to a (x,y) for some types, x, y */
      // case g2: Flip[a,b] => MapF(f andThen (_.swap))
      // case Flip() => MapF(f andThen (_.swap))
      case _ => Pipe(this,g)
    }
  }
  case class Par[A,B,C,D](f: F[A,B], g: F[C,D]) extends F[(A,C), (B,D)] {
    def eval = l => f.eval(l.map(_._1)) zip g.eval(l.map(_._2))
    override def pipe[E](h: F[(B,D),E]) = h match {
      case Par(f2, g2) => Par(f pipe f2, g pipe g2)
      /** This code does not compile  */
      // case Pipe(Flip(), Par(g2,f2)) => Par(f pipe f2, g pipe g2) pipe Flip()
      case _ => Pipe(this, h)
    }
  }
  case class Flip[A,B]() extends F[(A,B),(B,A)] {
    def eval = _ map { case (a,b) => (b,a) }
  }
  case class Pipe[A,B,C](f: F[A,B], g: F[B,C]) extends F[A,C] {
    def eval = f.eval andThen g.eval
  }

  object GADT2 {
    def main(args: Array[String]): Unit = {
      val x = MapF((x: Int) => x+1)
      val y = MapF((x: Int) => x*2)
      val z = x.pipe(y).eval(List(1,2,3))
      println(z)
    }
  }

}
