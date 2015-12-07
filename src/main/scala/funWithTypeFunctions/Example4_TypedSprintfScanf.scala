package funWithTypeFunctions

object TypedSprintfScanf {

  // the format string DSL
  trait L
  trait V[Val]
  trait C[f1, f2]

  type Parser[A] = String => List[(A, String)]
  type Printer[A] = A => String

  trait F[f]
  case class Lit(s: String) extends F[L]
  case class Val[Val](parser: Parser[Val], printer: Printer[Val]) extends F[V[Val]]
  case class Cmp[f1, f2](f1: F[f1], f2: F[f2]) extends F[C[f1, f2]]

  // the impl
  trait TPrinter[f, X] { type T }
  class TPrinterL[X] extends TPrinter[L, X] { type T = X }
  implicit def tprinterL[X]: TPrinter[L, X] = new TPrinterL
  class TPrinterV[X, Val] extends TPrinter[V[Val], X] { type T = Val => X }
  implicit def tprinterV[X, Val]: TPrinter[V[Val], X] = new TPrinterV
  class TPrinterC[X, f1, f2] extends TPrinter[C[f1, f2], X] { type T = TPrinter[f1, TPrinter[f2, X]] }
  implicit def tprinterC[X, f1, f2]: TPrinter[C[f1, f2], X] = new TPrinterC
  


  type SPrintf[f] = TPrinter[f, String]

  def sprintf[f](fmt: F[f]): SPrintf[f] = ???

}
