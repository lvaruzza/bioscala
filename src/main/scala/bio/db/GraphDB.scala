package bio.db

import bio.velvet.ReadGraph
import org.neo4j.graphdb._
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl
import org.neo4j.kernel.EmbeddedGraphDatabase
import scala.collection.JavaConversions._
import scala.io.Source
import org.neo4j.index.impl.lucene.LuceneBatchInserterIndexProvider

/*
 * Graph Database
 */
class GraphDB(db: GraphDatabaseService) {

	def list() {
		val nodes = db.getAllNodes
		for (node <- nodes) {
			println("node = " + node)
		}
	}
	
	
  def close { db.shutdown }
}

object GraphDB {
  def openDB(filename: String) = {
    val db: GraphDatabaseService = new EmbeddedGraphDatabase(filename);
    new GraphDB(db)
  }

  def importFile(dbfilename: String, filename: String) {
    val (header, things) = ReadGraph.readGraph(Source.fromFile(filename))
    val inserter = new BatchInserterImpl(dbfilename, BatchInserterImpl.loadProperties("neo4j.props"));
    // create the batch index service
    val indexProvider = new LuceneBatchInserterIndexProvider( inserter );

    val indexService = indexProvider.nodeIndex( "actors", Map( "type" -> "exact" ) );
    indexService.setCacheCapacity( "id", 100000 );
    
    println("Creating nodes")
    var nodeCount = 1

    for (thing <- things) {
      if (thing.isInstanceOf[bio.velvet.Node]) {
        val vnode = thing.asInstanceOf[bio.velvet.Node]

        val id = new java.lang.Integer(vnode.id)

        val nodePos = inserter.createNode(Map("id" -> id,
        									  "end" -> vnode.endPos ))
        indexService.add(nodePos,Map("id" -> id))
        
        val idNeg = new java.lang.Integer(-vnode.id)
        val nodeNeg = inserter.createNode(Map("id" -> idNeg,
        									  "end" -> vnode.endNeg))
        									  
        indexService.add(nodeNeg,Map("id" -> idNeg))

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
    Console.out.flush()
    indexService.flush()

    println("Creating edges")
    Console.out.flush
    var arcCount = 1

    val (header2, things2) = ReadGraph.readGraph(Source.fromFile(filename))
    val arcRel = DynamicRelationshipType.withName("ARC")
    val loopRel = DynamicRelationshipType.withName("LOOP")
    println("rel = " + arcRel)

    for (thing <- things2) {
      if (thing.isInstanceOf[bio.velvet.Arc]) {
        val arc = thing.asInstanceOf[bio.velvet.Arc]
        if (arc.startNode != arc.endNode) {
          val id1 = new java.lang.Integer(arc.startNode)
          val id2 = new java.lang.Integer(arc.endNode)
          val node1 = indexService.get("id", id1).iterator.next.asInstanceOf[scala.Long]
          val node2 = indexService.get("id", id2).iterator.next.asInstanceOf[scala.Long]
          inserter.createRelationship(node1, node2, arcRel, null);
 
          val id3 = new java.lang.Integer(-arc.startNode)
          val id4 = new java.lang.Integer(-arc.endNode)
          val node3 = indexService.get("id", id3).iterator.next.asInstanceOf[scala.Long]
          val node4 = indexService.get("id", id4).iterator.next.asInstanceOf[scala.Long]
          inserter.createRelationship(node3, node4, arcRel, null);
          
        } else {
        	val id = new java.lang.Integer(arc.startNode)
        	val node = indexService.get("id", id).iterator.next.asInstanceOf[scala.Long]

        	val mirrorNode = inserter.createNode(Map("mirror" -> new java.lang.Boolean(true)))

        	inserter.createRelationship(node, mirrorNode, loopRel, null);
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

    println("\nDatabase Shutdown")
    inserter.shutdown()
    indexProvider.shutdown()
  }

}