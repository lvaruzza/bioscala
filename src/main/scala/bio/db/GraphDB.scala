package bio.db

import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.graphdb.GraphDatabaseService

class GraphDB(db:GraphDatabaseService) {
	
	def close { db.shutdown }
}

object GraphDB {
	def openDB(filename:String) = {
		val db:GraphDatabaseService = new EmbeddedGraphDatabase( filename );
		new GraphDB(db)
	}
}