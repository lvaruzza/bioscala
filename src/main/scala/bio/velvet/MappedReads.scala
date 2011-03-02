package bio.velvet

import scala.io.Source
import java.io.File
import bio.Color
import bio.velvet.ReadGraph.readGraph
import bio.ReadFasta.readFasta
import bio.BioSeq
import bio.db.SeqDB

object MappedReads {

  def mappedReads(things: Iterator[Thing],
    seqs: SeqDB,
    contigs: SeqDB,
    colors: SeqDB) = {

    for (thing <- things) {
      if (thing.isInstanceOf[NR]) {
        val nr = thing.asInstanceOf[NR]
        println("=" * 50)
        println("Node " + nr.nodeId)
        contigs.find(nr.nodeId.abs) match {
          case Some(idxseq) => 
          	//println("  " + idxseq.seq.text)
          	println("  " + Color.de2color(idxseq.seq.text))
          case None => println(None)
        }
        for (read <- nr.reads) {
          //print("\t" + read.readId + " " + read.offsetFromStart + " " + read.startCoord)
          seqs.find(read.readId) match {
            case Some(idxseq) =>
              if (read.offsetFromStart >= 0) {                
                val seqName = idxseq.seq.name.split(Array(' ', '\t'))(0)
                colors.find(seqName) match {
                  case Some(idxColor) => println((" " * (read.offsetFromStart)) + idxColor.seq.text.drop(read.startCoord))
                  case None => println((" " * (2 + read.offsetFromStart)) + idxseq.seq.text.drop(read.startCoord))
                }
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

  def openAndImportInt(source: String, dbname: String, f: (BioSeq => Int)): SeqDB = {
    val dbfile = new File(dbname)
    if (dbfile.exists) dbfile.delete
    val db = SeqDB.openDB(dbname)
    dbfile.deleteOnExit
    db.importFastaInt(Source.fromFile(source), f);

    db
  }

  def openAndImportStr(source: String, dbname: String, f: (BioSeq => String)): SeqDB = {
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

  def main(args: Array[String]) {
    if (args.length > 0) {
      val graphFile = args(0) + "/LastGraph"
      val seqsFile = args(0) + "/Sequences"
      val contigsFile = args(0) + "/contigs.fa"
      val colorFile = args(1)

      println("Graph file: " + graphFile)
      println("Seqs file: " + seqsFile)
      println("Contigs file: " + contigsFile)
      println("color file: " + colorFile)

      val contigsdb = openAndImportInt(contigsFile, "contigs.db4o", x => nodeNumExtract(x.name))
      //SeqDB.listFasta(contigsdb);        
      val seqdb = openAndImportInt(seqsFile, "seqs.db4o", _.idx)
      val colordb = openAndImportStr(colorFile, "color.db4o", _.name)

      val (header, things) = readGraph(Source.fromFile(graphFile))

      mappedReads(things, seqdb, contigsdb, colordb)
      seqdb.close
    }
  }
}

