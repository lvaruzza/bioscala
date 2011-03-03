package bio.velvet

import scala.io.Source
import java.io.File
import bio.Color
import bio.velvet.ReadGraph.readGraph
import bio.ReadFasta.readFasta
import bio.BioSeq
import bio.db.SeqDB
import java.io.PrintStream

object DumpContigs extends ContigsWalker {
  def dumpContig(out: PrintStream, nr: NR, contig: BioSeq, seqs: SeqDB, colors: SeqDB) {
    val colorSeq = Color.de2color(contig.text)
    if (nr.nodeId < 0) {
      out.println(" " + colorSeq.reverse)
    } else {
      out.println(" " + colorSeq)
    }

    for (read <- nr.reads.sortBy(_.offsetFromStart)) {
      seqs.find(read.readId) match {
        case Some(idxseq) => {
          val seqName = idxseq.seq.name.split(Array(' ', '\t'))(0)
          if (read.offsetFromStart >= 0) {
            colors.find(seqName) match {
              case Some(idxColor) => out.println((" " * (read.offsetFromStart)) + Color.decodeFirst(idxColor.seq.text).drop(read.startCoord))
              case None => out.println((" " * (1 + read.offsetFromStart)) + idxseq.seq.text.drop(read.startCoord))
            }
          }
        }
        case _ => throw new Exception("Read %d not found".format(read.readId))
      }
    }
  }

  def dumpContigs(things: Iterator[Thing],
    seqs: SeqDB,
    contigs: SeqDB,
    colors: SeqDB,
    output: File) = {

    nrWithContigWalker(things, contigs) { (nr, seq) =>
      val out = new PrintStream(
        new File(output.getAbsolutePath + "/" +
          "node%d.txt".format(nr.nodeId)))
      println("Processing Node %d".format(nr.nodeId))
      dumpContig(out, nr, seq, seqs, colors)
      out.close
    }
  }

  
  def main(args: Array[String]) {
    if (args.length > 0) {
      val graphFile = args(0) + "/LastGraph"
      val seqsFile = args(0) + "/Sequences"
      val contigsFile = args(0) + "/contigs.fa"
      val colorFile = args(1)
      val outputDir = args(2)

      println("Graph file: " + graphFile)
      println("Seqs file: " + seqsFile)
      println("Contigs file: " + contigsFile)
      println("color file: " + colorFile)

      val contigsdb = importContigs(contigsFile) 
      //SeqDB.listFasta(contigsdb);        
      val seqdb = openAndImportInt(seqsFile, "seqs.db4o", _.idx)
      val colordb = openAndImportStr(colorFile, "color.db4o", _.name)

      val (header, things) = readGraph(Source.fromFile(graphFile))

      val output = new File(outputDir);
      if (!output.exists) {
        output.mkdirs;
      }
      dumpContigs(things, seqdb, contigsdb, colordb, output)
      seqdb.close
    }
  }
}

