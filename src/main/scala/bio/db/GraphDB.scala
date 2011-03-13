package bio.db

import org.neo4j.kernel.impl.batchinsert.BatchInserter
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl
import org.neo4j.index.lucene.LuceneIndexBatchInserter
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.graphdb._
import bio.velvet.ReadGraph
import scala.io.Source
import java.util.HashMap

/*
 * Graph Database
 */
class GraphDB(db: GraphDatabaseService) {

  def close { db.shutdown }
}

object GraphDB {
  class MyRelationshipTypes extends Enumeration with RelationshipType {
    val Link = Value
  }

  def openDB(filename: String) = {
    val db: GraphDatabaseService = new EmbeddedGraphDatabase(filename);
    new GraphDB(db)
  }

  def importFile(dbfilename: String, filename: String) {
    val (header, things) = ReadGraph.readGraph(Source.fromFile(filename))
    val inserter = new BatchInserterImpl(dbfilename, BatchInserterImpl.loadProperties("neo4j.props"));
    // create the batch index service
    val indexService = new LuceneIndexBatchInserterImpl(inserter);

    println("Creating nodes")
    var nodeCount = 1

    for (thing <- things) {
      if (thing.isInstanceOf[bio.velvet.Node]) {
        val properties = new HashMap[String, java.lang.Object]();
        val vnode = thing.asInstanceOf[bio.velvet.Node]
        val id = new java.lang.Integer(vnode.id)
        properties.put("id", id);
        val node = inserter.createNode(properties)
        indexService.index(node, "id", id);

        val properties2 = new HashMap[String, java.lang.Object]();
        val id2 = new java.lang.Integer(-vnode.id)
        properties2.put("id", id2);
        val node2 = inserter.createNode(properties2)
        indexService.index(node2, "id", id2);

        if (nodeCount % 100 == 0) {
          print(".")
          Console.out.flush()
        }
        if (nodeCount % (50 * 100) == 0) {
          println("\t%d".format(nodeCount))
        }
        nodeCount += 1
      }
    }
    println("\nOptimizing Index")
    Console.out.flush
    indexService.optimize()

    println("Creating edges")
    Console.out.flush
    var arcCount = 1

    val (header2, things2) = ReadGraph.readGraph(Source.fromFile(filename))
    val rel = DynamicRelationshipType.withName("KNOWS")
    println("rel = " + rel)

    for (thing <- things2) {
      if (thing.isInstanceOf[bio.velvet.Arc]) {
        val arc = thing.asInstanceOf[bio.velvet.Arc]
        if (arc.startNode != arc.endNode) {
          val id1 = new java.lang.Integer(arc.startNode)
          val id2 = new java.lang.Integer(arc.endNode)
          val node1 = indexService.getNodes("id", id1).iterator.next.asInstanceOf[scala.Long]
          val node2 = indexService.getNodes("id", id2).iterator.next.asInstanceOf[scala.Long]
          inserter.createRelationship(node1, node2, rel, null);
 
          val id3 = new java.lang.Integer(-arc.startNode)
          val id4 = new java.lang.Integer(-arc.endNode)
          val node3 = indexService.getNodes("id", id3).iterator.next.asInstanceOf[scala.Long]
          val node4 = indexService.getNodes("id", id4).iterator.next.asInstanceOf[scala.Long]
          inserter.createRelationship(node3, node4, rel, null);
          
        } else {
        	println("Invalid Loop Arc on node %d".format(arc.startNode))
        }
        
        if (arcCount % 100 == 0) {
          print(".")
          Console.out.flush()
        }
        if (arcCount % (50 * 100) == 0) {
          println("\t%d".format(arcCount))
        }
        arcCount += 1
      }
    }

    println("Database Shutdown")
    inserter.shutdown()
    indexService.shutdown()
  }

}