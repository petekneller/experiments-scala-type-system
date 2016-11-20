package familyPolymorphism

// My port of the paper 'Family Polymorphism' by Erik Ernst
// Read this in conjunction with the type-member driven approach in MartinKneissl in this package

object ErikErnst {

  trait AbstractNode[N <: AbstractNode[_, _], E <: AbstractEdge[_, _]] {
    def touches(edge: E): Boolean = edge.n1 == this || edge.n2 == this
  }

  abstract class AbstractEdge[E <: AbstractEdge[_, _], N <: AbstractNode[_, _]](val n1: N, val n2: N)

  abstract class Graph[G <: Graph[_, _, _], N <: AbstractNode[_, _], E <: AbstractEdge[_, _]] {
    def mkNode() : N
    def connect(n1: N, n2: N) : E
  }



  class BasicNode extends AbstractNode[BasicNode, BasicEdge]

  class BasicEdge(n1: BasicNode, n2: BasicNode) extends AbstractEdge[BasicEdge, BasicNode](n1, n2)

  class BasicGraph extends Graph[BasicGraph, BasicNode, BasicEdge] {
    def mkNode() = new BasicNode
    def connect(n1: BasicNode, n2: BasicNode) : BasicEdge = new BasicEdge(n1, n2)
  }



  class OnOffNode extends AbstractNode[OnOffNode, OnOffEdge] {
    override def touches(edge: OnOffEdge): Boolean = edge.enabled && super.touches(edge)
  }

  class OnOffEdge(n1: OnOffNode, n2: OnOffNode, var enabled: Boolean) extends AbstractEdge[OnOffEdge, OnOffNode](n1, n2)

  class OnOffGraph extends Graph[OnOffGraph, OnOffNode, OnOffEdge] {
    def mkNode() = new OnOffNode
    def connect(n1: OnOffNode, n2: OnOffNode) : OnOffEdge = new OnOffEdge(n1, n2, true)

  }

  // Using path-dependent types, you can't link two nodes from different graphs of the same type
  // However, under this approach this restriction can't be enforced
  val g = new BasicGraph
  val n1 = g.mkNode()
  val n2 = g.mkNode()
  val e = g.connect(n1,n2)
  assert(n1 touches e)
  assert(n2 touches e)
  val g2 = new BasicGraph
  g2.connect(n1,n2) // See?!


  val og = new OnOffGraph
  val on1 = og.mkNode()
  val on2 = og.mkNode()
  val oe = og.connect(on1, on2)
  // val mixed = og.connect(n1, n2) // ERROR: og.connect not applicable to g.Node

  assert(on1 touches oe)
  assert(on2 touches oe)
  // println(on2 touches e) // ERROR: on2.touches not applicable to g.Edge
  oe.enabled = false;
  assert (! (on2 touches oe), "After disabling, edge virtually has gone")
  assert (! (on1 touches oe), "After disabling, edge virtually has gone")

  def addSome[N <: AbstractNode[_, _], E <: AbstractEdge[_, _]](graph: Graph[_, N, E]): E = {
    val n1, n2 = graph.mkNode()
    graph.connect(n1,n2)
  }

  val e2 = addSome(g)
  val oe2 = addSome(og)
  // e2.enabled = false // type OnOffGraph not retained, graph.Edge not possible

  // Huh... not sure how to make this work!
  def addSome2[E <: AbstractEdge[_, _], N <: AbstractNode[_, _], G <: Graph[_, N, E]](graph: G): E = {
    val n1, n2 = graph.mkNode()
    graph.connect(n1,n2)
  }

  // val e2b = addSome2(g)
  // val oe2b = addSome2(og)
  // oe2b.enabled = false
}
