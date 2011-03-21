package bio.velvet
import bio.db.SeqDB
import java.io.File
import bio._
import scala.io.Source
import bio.velvet.ReadGraph.readGraph

/*
 * 	Common functions for velvet file reading
 * 
 */

trait VelvetReader {

  def openAndImportInt(source: String, dbname: String, f: (IndexedBioSeq => Int)): SeqDB = {
    println("Reading file " + source)
    val dbfile = new File(dbname)
    if (dbfile.exists) {
    	SeqDB.openDB(dbname)    	
    } else {
    	val db = SeqDB.openDB(dbname)
    	db.importFastaInt(Source.fromFile(source), f);
    }
  }

  def openAndImportStr(source: String, dbname: String, f: (IndexedBioSeq => String)): SeqDB = {
    println("Reading file " + source)
    val dbfile = new File(dbname)
    if (dbfile.exists) {
    	SeqDB.openDB(dbname)
    } else {
    	SeqDB.openDB(dbname).importFastaStr(Source.fromFile(source), f);
    }
  }

  val NodeRe = """^NODE_(\d+).*""".r

  def nodeNumExtract(name: String): Int = {
    name match {
      case NodeRe(x) => x.toInt
      case _ => -1
    }
  }

  def importContigs(contigsFile: String) = openAndImportInt(contigsFile, "contigs.db4o", x => nodeNumExtract(x.name))

  def nrWalker(things: Iterator[Thing])(f: (NR => Unit)) {
    for (thing <- things if thing.isInstanceOf[NR]) {
      f(thing.asInstanceOf[NR])
    }
  }

  def nrWithContigWalker(things: Iterator[Thing], contigs: SeqDB)(f: ((NR, BioSeq) => Unit)) {
    nrWalker(things) { nr =>
      contigs.find(nr.nodeId.abs) match {
        case Some(idxseq) => f(nr, idxseq.seq)
        case None => ()
      }
    }
  }

  def withColor(read: ReadPos, seqs: SeqDB, colors: SeqDB)(f: BioSeq => Unit) {
    seqs.find(read.readId) match {
      case Some(idxseq) => {
        val seqName = idxseq.seq.name.split(Array(' ', '\t'))(0)
        if (read.offsetFromStart >= 0) {
          colors.find(seqName) match {
            case Some(idxColor) => f(idxColor.seq)
            case None => None
          }
        }
      }
      case _ => throw new Exception("Read %d not found".format(read.readId))
    }
  }

  def readInputFiles(velvetDir: String, colorReads: String) = {
    val graphFile = velvetDir + "/LastGraph"
    val seqsFile = velvetDir + "/Sequences"
    val contigsFile = velvetDir + "/contigs.fa"
    val colorFile = colorReads

    println("Graph file: " + graphFile)
    println("Seqs file: " + seqsFile)
    println("Contigs file: " + contigsFile)
    println("color file: " + colorFile)

    val contigsdb = importContigs(contigsFile)
    //SeqDB.listFasta(contigsdb);        
    val seqdb = openAndImportInt(seqsFile, "seqs.db4o", _.idx)
    val colordb = openAndImportStr(colorFile, "color.db4o", _.name)

    val (header, things) = readGraph(Source.fromFile(graphFile))

    (header, things, contigsdb, seqdb, colordb)
  }
  
  def getOutput(outputDir:String) = {      
      val output = new File(outputDir);
      if (!output.exists) {
        output.mkdirs;
      }
      output
  }
    
}