import scala.io.Source

import bio.ReadGraph.readGraph
import bio.ReadFasta.readFasta
import bio.BioSeq
import bio.db.SeqDB
import com.db4o._
import java.io.File

package bio {
  object MappedReads {

    def mappedReads(things: Iterator[Thing], seqs: ObjectContainer, contigs: ObjectContainer) = {

      for (thing <- things) {
        if (thing.isInstanceOf[NR]) {
          val nr = thing.asInstanceOf[NR]
          println("=" * 50)
          println("Node " + nr.nodeId)
          SeqDB.getSeq(contigs, nr.nodeId.abs) match {
        	  case Some(idxseq) => println(idxseq.seq.text)
        	  case None => println(None)
          }
          for (read <- nr.reads) {
            //print("\t" + read.readId + " " + read.offsetFromStart + " " + read.startCoord)
            SeqDB.getSeq(seqs, read.readId) match {
              case Some(idxseq) =>
                if (read.offsetFromStart >= 0) {
                  println((" " * read.offsetFromStart) + idxseq.seq.text.drop(read.startCoord))
                  //println("\t" + read.readId + " " + read.offsetFromStart + " " + read.startCoord + " " + seq.text)
                }
              case _ => println("read " + read.readId + " not found")
            }
          }
          println("*" * 50)          
        } else if (thing.isInstanceOf[SEQ]) {
          val seq = thing.asInstanceOf[SEQ]
        }
      }
    }

    def openAndImport(source: String, dbname: String, f: (BioSeq => Int)): ObjectContainer = {
      val dbfile = new File(dbname)
      if (dbfile.exists) dbfile.delete
      val db = SeqDB.openDB(dbname)
      dbfile.deleteOnExit
      SeqDB.importFasta(db, Source.fromFile(source), f);

      db
    }

    val NodeRe = """^NODE_(\d+).*""".r

    def nodeNumExtract(name: String): Int = {
      name match {
        case NodeRe(x) => x.toInt
        case _ => -1
      }
    }

    def main(args: Array[String]) {
      if (args.length > 0) {
        val graphFile = args(0) + "/LastGraph"
        val seqsFile = args(0) + "/Sequences"
        val contigsFile = args(0) + "/contigs.fa"

        println("Graph file: " + graphFile)
        println("Seqs file: " + seqsFile)
        println("Contigs file: " + contigsFile)

        val contigsdb = openAndImport(contigsFile, "contigs.db4o", x => nodeNumExtract(x.name))
        //SeqDB.listFasta(contigsdb);        
        val seqdb = openAndImport(seqsFile, "seqs.db4o", _.idx)

        val (header, things) = readGraph(Source.fromFile(graphFile))
        
        mappedReads(things, seqdb, contigsdb)
        seqdb.close
      }
    }
  }
}
