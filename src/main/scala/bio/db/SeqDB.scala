package bio.db

import scala.io.Source
import bio.ReadFasta.readFasta
import com.db4o._
import bio.BioSeq

object SeqDB {
	def openDB(dbFile:String) = {
		val config = Db4oEmbedded.newConfiguration
		config.common.objectClass(classOf[BioSeq]).objectField("idx").indexed(true);
		Db4oEmbedded.openFile(config,dbFile);		
	}
	
	def importFasta(db:ObjectContainer,in:Source) {
		val seqs = readFasta(in);
		
		for(seq <-seqs ) {
			db store seq
		}
		
		db.commit;
	}
	
	def listFasta(db:ObjectContainer) {
		val q = db.query();
		q.constrain(classOf[BioSeq]);
		
		val res = q.execute;
		while(res.hasNext) {
			println("R: " + res.next)
		}
	}
	def getSeq(db:ObjectContainer,readId:Int):BioSeq = {
		val q = db.query
		q.constrain(classOf[BioSeq])
		q.descend("idx").constrain(readId);
		val res = q.execute;
		return res.next
	}
	
	def main(args:Array[String]) {		
		val db=openDB(args(1))
		importFasta(db,Source.fromFile(args(0)))
		println(getSeq(db,1));
		db.close
	}
}