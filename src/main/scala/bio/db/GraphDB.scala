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

class MyRelationshipTypes extends Enumeration with RelationshipType {
  val Link = Value
}

/*
 * Graph Database
 */
class GraphDB(db: GraphDatabaseService) {

  def close { db.shutdown }
}

object GraphDB {
  def openDB(filename: String) = {
    val db: GraphDatabaseService = new EmbeddedGraphDatabase(filename);
    new GraphDB(db)
  }

  def importFile(dbfilename:String, filename: String) {
    val (header, things) = ReadGraph.readGraph(Source.fromFile(filename))

    val inserter = new BatchInserterImpl(dbfilename, BatchInserterImpl.loadProperties("neo4j.props"));
    // create the batch index service
    val indexService = new LuceneIndexBatchInserterImpl(inserter);

    for (thing <- things) {
      if (thing.isInstanceOf[bio.velvet.Node]) {
    	val properties = new HashMap[String,Object]();
  
        val vnode = thing.asInstanceOf[bio.velvet.Node]
        val node = inserter.createNode(properties)
        indexService.index( node, "id", vnode.id );
      }
    }
    indexService.optimize()
    
    val (header2, things2) = ReadGraph.readGraph(Source.fromFile(filename))
    for (thing <- things) {
    	if (thing.isInstanceOf[bio.velvet.Arc]) {
    		val arc = thing.asInstanceOf[bio.velvet.Arc]
    		val node1 = indexService.getNodes("id",arc.startNode ).iterator.next.asInstanceOf[scala.Long]
    		val node2 = indexService.getNodes("id",arc.endNode).iterator.next.asInstanceOf[scala.Long]
    		inserter.createRelationship( node1, node2, DynamicRelationshipType.withName( "Link" ), null );
    		val node3 = indexService.getNodes("id",-arc.startNode).iterator.next.asInstanceOf[scala.Long]
    		val node4 = indexService.getNodes("id",-arc.endNode).iterator.next.asInstanceOf[scala.Long]
    		inserter.createRelationship( node3, node4, DynamicRelationshipType.withName( "L" ), null );
    	}
    }
  }

}