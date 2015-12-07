package variableArity

import org.scalatest.FunSuite

class VariableArityLoansTests extends FunSuite {

  def withInt(f: Int => Unit): Unit = { f(2) }

  def withString(f: String => Unit): Unit = { f("foo!") }

  def withBoolean(f: Boolean => Unit): Unit = { f(false) }

  def withSomeStuff2[A, B](l1: (A => Unit) => Unit, l2: (B => Unit) => Unit)(block: (A, B) => Unit): Unit = {
    l1{ (a: A) =>
      l2{ (b: B) =>
        block(a, b)
      }
    }
  }

  def withSomeStuffSeq[A](loans: List[(A => Unit) => Unit])(block: List[A] => Unit): Unit = {
    def helper(loans: List[(A => Unit) => Unit], args: List[A]): Unit = {

      loans match {
        case Nil => block(args)
        case l :: rest => l{ (arg: A) =>
          helper(rest, args :+ arg)
        }
      }
    }

    helper(loans, Nil)
  }

//  def withSomeStuff[A, B](block: A => B)(loan: (A => Unit) => Unit): ((Int => Unit) => Unit) => ((String => Unit) => Unit) => Unit = {
//    ???
//
//    val t = loan{ (a: A) =>
//      block(a)
//    }
//
//    //withSomeStuff({ (b: B) =>  })(_)
//
//    { (f: ((B => Unit)=> Unit)) => () }
//  }

  test("variable-arity loan pattern") {
//    withSomeStuff2(withInt, withBoolean) { (i: Int, b: Boolean) =>
//      println(i)
//      println(b)
//    }

//    withSomeStuffSeq(List(withInt _, withBoolean _)) { case (i: Int) :: (b: Boolean) :: Nil =>
//      println(i)
//      println(b)
//    }

    // of course the above relies upon the correct lining-up of types in the argument sequences
//    withSomeStuffSeq(List(withString _, withBoolean _)) { case (i: Int) :: (b: Boolean) :: Nil =>
//      println(i)
//      println(b)
//    }

//    val block = { (i: Int, b: Boolean) =>
//      println(i)
//      println(b)
//    }
//
//    val block2: (Int) => (Boolean) => Unit = block.curried
//    val wss = withSomeStuff(block2)(withInt _)
//    withSomeStuff(block2)(withInt _)(withBoolean _)
  }
}
