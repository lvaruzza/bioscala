package bio.db

import scala.io.Source
import bio.ReadFasta.readFasta
import com.db4o._
import bio.BioSeq
import com.db4o.internal.InternalObjectContainer
import com.db4o.internal.query.Db4oQueryExecutionListener
import com.db4o.internal.query.NQOptimizationInfo

case class IntIndexedBioSeq(seq:BioSeq,idx:Int)
case class StringIndexedBioSeq(seq:BioSeq,idx:String)

class MyDb4oQueryExecutionListener extends Db4oQueryExecutionListener() {
	def notifyQueryExecuted(info:NQOptimizationInfo) {
		println(info);
    }
}

class SeqDB(db:ObjectContainer) {
	
	def importFastaInt(in:Source,f:(BioSeq => Int)):SeqDB = {
		val seqs = readFasta(in);
		var i = 1;
		for(seq <-seqs ) {
			db store (new IntIndexedBioSeq(seq,f(seq)))
			if (i%1000==0) {
				print(".")
				Console.out.flush
				db.commit;				
			}
			i += 1;
		}
		println
		db.commit
		
		this
	}

	def importFastaStr(in:Source,f:(BioSeq => String)) = {
		val seqs = readFasta(in);
		var i = 1;
		for(seq <-seqs ) {
			db store (new StringIndexedBioSeq(seq,f(seq)))
			if (i%1000==0) {
				print(".")
				Console.out.flush				
				db.commit;				
			}
			i += 1;
		}
		println
		db.commit
		
		this
	}
	
	
	def listFasta() {
		val q = db.query();
		q.constrain(classOf[BioSeq]);
		
		val res = q.execute;
		while(res.hasNext) {
			println("R: " + res.next)
		}
	}

	def find(idx:Int):Option[IntIndexedBioSeq] = {
		val q = db.query
		q.constrain(classOf[IntIndexedBioSeq])
		q.descend("idx").constrain(idx);
		val res = q.execute;
		
		if (res.hasNext) Some(res.next)  else None
	}

	def find(idx:String):Option[StringIndexedBioSeq] = {
		val q = db.query
		q.constrain(classOf[StringIndexedBioSeq])
		q.descend("idx").constrain(idx);
		val res = q.execute;
		
		if (res.hasNext) Some(res.next)  else None
	}
	
	def close { db.close }
}

object SeqDB {
	def openDB(dbFile:String) = {
		val config = Db4oEmbedded.newConfiguration
		config.common.objectClass(classOf[IntIndexedBioSeq]).objectField("idx").indexed(true);
		config.common.objectClass(classOf[StringIndexedBioSeq]).objectField("idx").indexed(true);
		val db = Db4oEmbedded.openFile(config,dbFile);
		(db.asInstanceOf[InternalObjectContainer]).getNativeQueryHandler().addListener(new MyDb4oQueryExecutionListener());
		new SeqDB(db)
	}
	
	def main(args:Array[String]) {		
		val db=openDB(args(1))
		db.importFastaInt(Source.fromFile(args(0)),_.idx)
		println(db find 1);
		db.close
	}
}