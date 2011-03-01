package bio.db

import scala.io.Source
import bio.ReadFasta.readFasta
import com.db4o._
import bio.BioSeq
import com.db4o.internal.InternalObjectContainer
import com.db4o.internal.query.Db4oQueryExecutionListener
import com.db4o.internal.query.NQOptimizationInfo

case class IndexedBioSeq(seq:BioSeq,idx:Int)

class MyDb4oQueryExecutionListener extends Db4oQueryExecutionListener() {
	def notifyQueryExecuted(info:NQOptimizationInfo) {
		println(info);
    }
}

object SeqDB {
	def openDB(dbFile:String) = {
		val config = Db4oEmbedded.newConfiguration
		config.common.objectClass(classOf[IndexedBioSeq]).objectField("idx").indexed(true);
		val db = Db4oEmbedded.openFile(config,dbFile);
		(db.asInstanceOf[InternalObjectContainer]).getNativeQueryHandler().addListener(new MyDb4oQueryExecutionListener());
		db
	}
	
	def importFasta(db:ObjectContainer,in:Source,f:(BioSeq => Int)) {
		val seqs = readFasta(in);
		var i = 1;
		for(seq <-seqs ) {
			db store (new IndexedBioSeq(seq,f(seq)))
			if (i%1000==0) db.commit;
			i += 1;
		}
		
	}
	
	def listFasta(db:ObjectContainer) {
		val q = db.query();
		q.constrain(classOf[BioSeq]);
		
		val res = q.execute;
		while(res.hasNext) {
			println("R: " + res.next)
		}
	}
	def getSeq(db:ObjectContainer,readId:Int):Option[BioSeq] = {
		val q = db.query
		q.constrain(classOf[BioSeq])
		q.descend("idx").constrain(readId);
		val res = q.execute;
		
		if (res.hasNext) Some(res.next)  else None
	}
	
	def main(args:Array[String]) {		
		val db=openDB(args(1))
		importFasta(db,Source.fromFile(args(0)),_.idx)
		println(getSeq(db,1));
		db.close
	}
}