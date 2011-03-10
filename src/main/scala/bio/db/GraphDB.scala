package bio.db

import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.graphdb._
import bio.velvet.ReadGraph
import scala.io.Source

class MyRelationshipTypes extends Enumeration with RelationshipType
{
    val Link = Value
}

class GraphDB(db:GraphDatabaseService) {
	def importFile(filename:String) {
		val (header,things) = ReadGraph.readGraph(Source.fromFile(filename))
		val tx = db.beginTx();
		
		try {
			for(thing <- things) {
				if (thing.isInstanceOf[bio.velvet.Node]) {
					val vnode = thing.asInstanceOf[bio.velvet.Node]
					val node = db.createNode
					node.setProperty("id", vnode.id)
				}
			}
			tx.success

		} finally {
			tx.finish
		}
	}
	def close { db.shutdown }
}

object GraphDB {
	def openDB(filename:String) = {
		val db:GraphDatabaseService = new EmbeddedGraphDatabase( filename );		
		new GraphDB(db)
	}
}