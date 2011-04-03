package bio.velvet
import bio.Color
import bio.db.SeqDB
import java.io._

@serializable
case class ContigAlign(val colorSeq:String,
					   val displacement:Int,
					   val strand:Char,
					   val baseDensity:Array[Array[Int]],
					   val colorDensity:Array[Array[Int]])

/*
 * Export Contigs and their alignments to a binary and a text file
 * 					   
 */
object ExportContigs extends VelvetReader {

  def exportContigs(things: Iterator[Thing], contigs: SeqDB, seqs: SeqDB, colors: SeqDB,outputDir:File) {
    def readSort(read: ReadPos) = read.offsetFromStart - read.startCoord - 1

    this.nrWithContigWalker(things, contigs) { (nr, contig) =>
      println("Processing node %d".format(nr.nodeId))
      val outTxt = new PrintStream(
        new File(outputDir.getAbsolutePath + "/" +
          "node%d.txt".format(nr.nodeId)))

      val outObj = new ObjectOutputStream(
        new FileOutputStream(outputDir.getAbsolutePath + "/" +
        					 "node%d.obj".format(nr.nodeId)))
      
      val colorSeqOrig = Color.de2color(contig.text)
      val colorSeq = if (nr.nodeId < 0) colorSeqOrig.reverse else colorSeqOrig
      val sortedReads = nr.reads.sortBy(readSort)
      val minPos = -readSort(sortedReads.head)
      outTxt.println(minPos + " " + sortedReads.head)
      outTxt.println((" " * minPos) + colorSeq)

      val extLen = minPos + colorSeq.length
      val extSeq = " "*minPos + colorSeq
      val baseDensity = Array.ofDim[Int](extLen,5)
      val colorDensity = Array.ofDim[Int](extLen,5)
    	  
      for (read <- sortedReads) {
        withColor(read, seqs, colors) { color =>
        	val i = read.offsetFromStart - read.startCoord - 1 + minPos
        	val colorRead = Color.decodeFirst(color.text)
        	baseDensity(i)(Color.base2num(colorRead(0))) += 1
        	for(j <- Range(1,colorRead.length-1)) {
        		if (i+j < extLen) colorDensity(i+j)(Color.color2num(colorRead(j)))+=1
        	}
        	outTxt.print((" " * i) + colorRead)
        	outTxt.println("\t" + read.offsetFromStart + " " + read.startCoord + " " + color.name)
        }
      }
      
      var i = 0
      while(i < baseDensity.length) {
    	  outTxt.println(extSeq(i) + ": " + baseDensity(i).mkString("\t") + "\t" + colorDensity(i).mkString("\t"))
    	  i+=1
      }
      
      outObj.writeObject(new ContigAlign(colorSeq,minPos,(if (nr.nodeId < 0) '-' else '+'),baseDensity,colorDensity))
      outTxt.close()
      outObj.close()
    }
  }

  def main(args: Array[String]) {
    if (args.length >= 3) {
      val (header, things, contigsdb, seqdb, colordb) = readInputFiles(args(0), args(1))
      
      val output = getOutput(args(2))
      
      exportContigs(things, contigsdb, seqdb, colordb,output)
    } else {
    	println("Invalid number of parameters")
    }
  }
}