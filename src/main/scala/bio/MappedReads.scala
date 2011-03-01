import scala.io.Source

import bio.ReadGraph.readGraph
import bio.ReadFasta.readFasta
import bio.BioSeq
import bio.db.SeqDB
import com.db4o._
import java.io.File

package bio {
  object MappedReads {

    def mappedReads(things: Iterator[Thing], seqs: ObjectContainer,contigs: ObjectContainer) = {

      for (thing <- things) {
        if (thing.isInstanceOf[NR]) {
          val nr = thing.asInstanceOf[NR]
          println("Node " + nr.nodeId)
          for (read <- nr.reads) {
            //print("\t" + read.readId + " " + read.offsetFromStart + " " + read.startCoord)
            val seq:BioSeq = SeqDB.getSeq(seqs, read.readId)
            if (read.offsetFromStart >= 0) {
            	println((" " * read.offsetFromStart) + seq.text.drop(read.startCoord))
            	//println("\t" + read.readId + " " + read.offsetFromStart + " " + read.startCoord + " " + seq.text)
            }
          }
        } else if (thing.isInstanceOf[SEQ]) {
          val seq = thing.asInstanceOf[SEQ]
        }
      }
    }

    def openAndImport(source:String,dbname:String):ObjectContainer = {
    	val dbfile = new File(dbname)
        if (dbfile.exists) dbfile.delete
        val db = SeqDB.openDB(dbname)
        dbfile.deleteOnExit        
        SeqDB.importFasta(db, Source.fromFile(source));
    	
    	db
    }
    
    def main(args: Array[String]) {
      if (args.length > 0) {
        val graphFile = args(0) + "/LastGraph"
        val seqsFile = args(0) + "/Sequences"
        val contigsFile = args(0) + "/contigs.fa"

        println("Graph file: " + graphFile)
        println("Seqs file: " + seqsFile)
        
        val (header, things) = readGraph(Source.fromFile(graphFile))
        val seqdb = openAndImport(seqsFile,"seqs.db4o")
        val contigsdb = openAndImport(contigsFile,"contigs.db4o")
        
        mappedReads(things, seqdb,contigsdb)
        seqdb.close
      }
    }
  }
}
