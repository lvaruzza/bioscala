package bio.velvet

import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

abstract class Thing()

case class Nothing() extends Thing

case class Node(val id: Int, val end: String, val endTwin: String,
  val covShort1: Int,
  val oCovShort1: Int,
  val covShort2: Int,
  val oCovShort2: Int) extends Thing

case class Arc(val startNode: Int, val endNode: Int, val multiplicity: Int)
  extends Thing

case class ReadPos(val readId: Int, val offsetFromStart: Int, val startCoord: Int)

case class NR(val nodeId: Int, val numberOfReads: Int, reads: Array[ReadPos])
  extends Thing

case class NodePos(val nodeId: Int, val offsetFromStart: Int,
  val startCoord: Int, val endCoord: Int, val offsetFromEnd: Int)

case class SEQ(val seqId: Int, nodes: Array[NodePos])
  extends Thing

class GraphIterator(lines:Iterator[String]) extends Iterator[Thing] {
   private val stateRegexp = """^([^ \t\n]+)""".r

	
	var line = ""

  def readState(line: String): Symbol = {
    val st = (stateRegexp.findFirstIn(line) match {
      case Some(x) => x match {
        case "NODE" => 'Node
        case "ARC" => 'Arc
        case "NR" => 'NR
        case "SEQ" => 'SEQ
        case _ => 'None
      }
      case None => 'None
    })
    st
  }

  def readNode = {
    val vals = line.split(Array('\t', ' ')).drop(1).map(x => x.toInt)
    val end = lines.next()
    val twin = lines.next()
    val node = new Node(vals(0), end, twin, vals(1), vals(2), vals(3), vals(4))
    //println(node)
    if (lines.hasNext)
      line = lines.next()
    node
  }

  def readArc = {
    val vals = line.split(Array('\t', ' ')).drop(1).map(_.toInt)
    val arc = new Arc(vals(0), vals(1), vals(2))
    //println(arc)
    if (lines.hasNext)
      line = lines.next()
    arc
  }

  def readNR = {
    val vals = line.split(Array('\t', ' ')).drop(1).map(_.toInt)
    var newLine = ""
    var newState = 'None
    var ab = new ArrayBuffer[ReadPos]()

    if (lines.hasNext) {
      do {
        newLine = lines.next
        newState = readState(newLine)
        if (newState == 'None) {
          val vals = newLine.split('\t').map(_.toInt)
          ab += new ReadPos(vals(0), vals(1), vals(2))
        }
      } while (newState == 'None && lines.hasNext)
      val nr = new NR(vals(0), vals(1), ab.toArray)
      line = newLine
      nr
    } else
      new NR(vals(0), vals(1), Array[ReadPos]())
  }


  def readSEQ  = {
    val vals = line.split(Array('\t', ' ')).drop(1).map(_.toInt)
    var newLine = ""
    var newState = 'None
    var ab = new ArrayBuffer[NodePos]()

    if (lines.hasNext) {
      do {
        newLine = lines.next
        newState = readState(newLine)
        if (newState == 'None) {
          val vals = newLine.split('\t').map(_.toInt)
          ab += new NodePos(vals(0), vals(1), vals(2), vals(3), vals(4))
        }
      } while (newState == 'None && lines.hasNext)
      val seq = new SEQ(vals(0), ab.toArray)
      line = newLine 
      seq
    } else
      new SEQ(vals(0), Array[NodePos]())
  }

  def readThing = {
    readState(line) match {
      case 'Node => readNode
      case 'Arc => readArc
      case 'NR => readNR
      case 'SEQ => readSEQ
      case _ => new Nothing()
    }
  }
		
	def hasNext = lines.hasNext
	
	def next = readThing
}

object ReadGraph {
  def str2ints(line: String, dropHead: Int) =
    line.split(Array('\t', ' ')).drop(dropHead).map(_.toInt)

  def readGraph(in: Source): (Array[Int], Iterator[Thing]) = {
    val lines = in.getLines

    val header = str2ints(lines.next, 0)

    (header, new GraphIterator(lines))
  }

  def main(args: Array[String]) {
    if (args.length > 0) {
      val (header, things) = readGraph(Source.fromFile(args(0)))

      println(header.mkString(","))
      for (thing <- things) {
        println(thing)
      }
    }
  }
}

