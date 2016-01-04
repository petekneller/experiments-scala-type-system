package familyPolymorphism

// from http://www.familie-kneissl.org/Members/martin/blog/family-polymorphism-in-scala
// which seems to now be dead, so inlining some of his comments from the blog
// motivated by the paper 'Family Polymorphism' by Erik Ernst

object MartinKneissl {

  abstract class Graph {
    type Node <: AbstractNode
    type Edge <: AbstractEdge

    def mkNode() : Node
    def connect(n1: Node, n2: Node) : Edge

    abstract class AbstractEdge(val n1: Node, val n2: Node)

    trait AbstractNode {
      def touches(edge: Edge): Boolean = edge.n1 == this || edge.n2 == this
    }
  }

  class BasicGraph extends Graph {
    type Node = BasicNode
    type Edge = BasicEdge
    protected class BasicNode extends AbstractNode
    protected class BasicEdge(n1:Node, n2:Node) extends AbstractEdge(n1, n2)

    def mkNode() = new BasicNode
    def connect(n1: Node, n2: Node) : BasicEdge = new BasicEdge(n1, n2)
  }

  /*
    OnOffGraph has the extension to provide for disabled edges. Note that the method OnOffNode.touches can be defined
    without the need to downcast the edge, because the abstract type Edge is defined to be an OnOffEdge in OnOffGraphs.
    The type system ensures that you cannot have a BasicEdge connected to OnOffNodes, so we are safe here!
   */

  class OnOffGraph extends Graph {
    type Node = OnOffNode
    type Edge = OnOffEdge
    protected class OnOffNode extends AbstractNode {
      override def touches(edge: Edge): Boolean = edge.enabled && super.touches(edge)
    }
    protected class OnOffEdge(n1:Node, n2:Node, var enabled: Boolean) extends AbstractEdge(n1, n2)
    def mkNode() = new OnOffNode
    def connect(n1: Node, n2: Node) : OnOffEdge = new OnOffEdge(n1, n2, true)

  }

  val g = new BasicGraph
  val n1 = g.mkNode()
  val n2 = g.mkNode()
  val e = g.connect(n1,n2)
  assert(n1 touches e)
  assert(n2 touches e)
  val g2 = new BasicGraph
  // g2.connect(n1,n2) // ERROR: can't link to node of other graph

  /*
    The way BasicGraphs are defined also prevents edges across graphs (Node and Edge are "path-dependent types").
    Whether this is desired depends on the context; one could define a super class and an OuterEdge parallel to
    BasicEdge that would allow to span different graphs.
   */

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

  /*
    As desired, edges can be disabled for OnOffGraphs and the different node types are not compatible such that
    attempts to disable BasicEdges or to access an "enabled" field via runtime type BasicEdge are statically prevented.
   */

  def addSome(graph: Graph): Graph#Edge = {
    val n1, n2 = graph.mkNode()
    graph.connect(n1,n2)
  }

  val e2 = addSome(g)
  val oe2 = addSome(og)
  // e2.enabled = false // type OnOffGraph not retained, graph.Edge not possible


  /*
    It is still possible to write code in terms of unspecific Graphs. In the current Scala implementation
    it is not possible to refer to a path-dependent type starting at a method parameter. Therefore, when the
    addSome method is applied to an OnOffGraph, the information that the result is always an OnOffEdge,
    is not retained by the type system. But you can get near that by making addSome generic:
   */

  def addSome2[G <: Graph](graph: G): G#Edge = {
    val n1, n2 = graph.mkNode()
    graph.connect(n1,n2)
  }

  val e2b = addSome2(g)
  val oe2b = addSome2(og)
  oe2b.enabled = false // now OK.

}
