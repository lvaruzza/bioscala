package bio.velvet
import bio.db.SeqDB
import java.io.File
import bio.BioSeq
import scala.io.Source

trait ContigsWalker {

  def openAndImportInt(source: String, dbname: String, f: (BioSeq => Int)): SeqDB = {
	  println("Reading file " + source)
	  val dbfile = new File(dbname)
	  if (dbfile.exists) dbfile.delete
	  val db = SeqDB.openDB(dbname)
	  dbfile.deleteOnExit
	  db.importFastaInt(Source.fromFile(source), f);
	   
	  db
  }

  def openAndImportStr(source: String, dbname: String, f: (BioSeq => String)): SeqDB = {
	println("Reading file " + source)
    val dbfile = new File(dbname)
    if (dbfile.exists) dbfile.delete
    val db = SeqDB.openDB(dbname)
    dbfile.deleteOnExit
    db.importFastaStr(Source.fromFile(source), f);

    db
  }

  val NodeRe = """^NODE_(\d+).*""".r

  def nodeNumExtract(name: String): Int = {
    name match {
      case NodeRe(x) => x.toInt
      case _ => -1
    }
  }

  def importContigs(contigsFile:String) = openAndImportInt(contigsFile, "contigs.db4o", x => nodeNumExtract(x.name))

  def nrWalker(things: Iterator[Thing])(f: (NR => Unit)) {
    for (thing <- things if thing.isInstanceOf[NR]) {
        f(thing.asInstanceOf[NR])
    }
  }
  
  def nrWithContigWalker(things: Iterator[Thing],contigs:SeqDB)(f:((NR,BioSeq) => Unit)) {
	  nrWalker(things) { nr =>
	  	contigs.find(nr.nodeId.abs) match {
	  		case Some(idxseq) => f(nr,idxseq.seq)
	  		case None => ()
	  	}	
	  }
  }
 
}