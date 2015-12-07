package funWithTypeFunctions

import org.scalatest.FunSuite
import TypedSprintfScanf._

class TypedPrintfScanfTest extends FunSuite {
  def int: F[V[Int]] = Val(???, _.toString)
  def f_ld: F[L] = Lit("day")
  def f_lds: F[C[L, L]] = Cmp(Lit("day"), Lit("s"))
  def f_dn: F[C[L, V[Int]]] = Cmp(Lit("day"), int)
  def f_nds: F[C[V[Int], C[L, L]]] = Cmp(int, Cmp(Lit("day"), Lit("s")))
  
  test("some sprintf examples") {
    //assert(sprintf(f_ld) === "day")
    //assert(sprintf(f_lds) === "days")
    //assert(sprintf(f_dn)(3) === "day 3")
    //assert(sprintf(f_nds)(3) === "3 days")
  }
}
