package bio.velvet

import bio.db.SeqDB
import bio.Color


object DecodeContigs extends ContigsWalker {

  def decodeContigs(things: Iterator[Thing], contigs: SeqDB, seqs: SeqDB, colors: SeqDB) {
    def readSort(read: ReadPos) = read.offsetFromStart - read.startCoord - 1

    this.nrWithContigWalker(things, contigs) { (nr, contig) =>
      val colorSeqOrig = Color.de2color(contig.text)
      val colorSeq = if (nr.nodeId < 0) colorSeqOrig.reverse else colorSeqOrig
      val sortedReads = nr.reads.sortBy(readSort)
      val minPos = -readSort(sortedReads.head)
      println(minPos + " " + sortedReads.head)
      println((" " * minPos) + colorSeq)

      val extLen = minPos + colorSeq.length
      val extSeq = " "*minPos + colorSeq
      val baseDensity = Array.ofDim[Int](extLen,4)
      val colorDensity = Array.ofDim[Int](extLen,4)
    	  
      for (read <- sortedReads) {
        withColor(read, seqs, colors) { color =>
        	val i = read.offsetFromStart - read.startCoord - 1 + minPos
        	println((i,read.offsetFromStart,read.startCoord,minPos))
        	val colorRead = Color.decodeFirst(color.text)
        	baseDensity(i)(Color.base2num(colorRead(0))) += 1
        	for(j <- Range(1,colorRead.length-1)) {
        		if (i+j < extLen) colorDensity(i+j)(colorRead(j) - '0')
        	}
        	print((" " * i) + colorRead)
        	println("\t" + read.offsetFromStart + " " + read.startCoord)
        }
      }
      var i = 0
      while(i < baseDensity.length) {
    	  println(extSeq(i) + ": " + baseDensity(i).mkString("\t") + "\t" + colorDensity(i).mkString("\t"))
    	  i+=1
      }
    }
  }

  def main(args: Array[String]) {
    if (args.length > 2) {
      val (header, things, contigsdb, seqdb, colordb) = readInputFiles(args(0), args(1))

      decodeContigs(things, contigsdb, seqdb, colordb)
    }
  }
}