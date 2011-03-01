package bio.db

import scala.io.Source
import bio.ReadFasta.readFasta
import com.db4o.Db4oEmbedded
import bio.BioSeq

object DBImport {

	def openDB(dbFile:String) = {
		val config = Db4oEmbedded.newConfiguration
		config.common.objectClass(classOf[BioSeq]).objectField("idx").indexed(true);
		Db4oEmbedded.openFile(config,dbFile);		
	}
	
	def importFasta(in:Source,dbFile:String) {
		val seqs = readFasta(in);
		val db = openDB(dbFile);
		
		for(seq <-seqs ) {
			db store seq
		}
		
		db.commit;
		db.close;
	}
	
	def listFasta(dbFile:String) {
		val db = openDB(dbFile);
		
		val q = db.query();
		q.constrain(classOf[BioSeq]);
		
		val res = q.execute;
		while(res.hasNext) {
			println("R: " + res.next)
		}
	}
	def main(args:Array[String]) {
		importFasta(Source.fromFile(args(0)),args(1))
		listFasta(args(1))
	}
}