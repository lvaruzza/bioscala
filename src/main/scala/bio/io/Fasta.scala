package bio.io

import scala.annotation.tailrec
import bio._

import scala.io.Source
import java.io.OutputStream
import java.io.File
import java.io.InputStream
import java.io.FileOutputStream


class FastaIterator(lines: Iterator[String]) extends Iterator[BioSeq] {
  private var lastLine = ""

  while (lines.hasNext && !lastLine.startsWith(">")) {
    lastLine = lines.next
  }

  def next: BioSeq = {
    val sb = new StringBuilder()
    val header = lastLine
    do {
      lastLine = lines.next
      if (!lastLine.startsWith(">")) sb.append(lastLine)
    } while (lines.hasNext && !lastLine.startsWith(">"))

    val seq = new SimpleBioSeq(header.stripPrefix(">"), sb.toString)
    seq
  }

  def hasNext = lines.hasNext
}

class IndexedFastaIterator(lines: Iterator[String]) extends Iterator[IndexedBioSeq] {
  private var lastLine = ""
  private var idx = 1

  while (lines.hasNext && !lastLine.startsWith(">")) {
    lastLine = lines.next
  }

  def next: IndexedBioSeq = {
    val sb = new StringBuilder()
    val header = lastLine
    do {
      lastLine = lines.next
      if (!lastLine.startsWith(">")) sb.append(lastLine)
    } while (lines.hasNext && !lastLine.startsWith(">"))

    val seq = new IndexedBioSeq(header.stripPrefix(">"), sb.toString, idx)
    idx += 1
    seq
  }

  def hasNext = lines.hasNext
}

class IndexedFastaReader extends Reader {
  override def read[IndexedBioSeq](in: Source) : Iterator[IndexedBioSeq] = {
    //return (new IndexedFastaIterator(in.getLines)).asIntanceOf[Iterator[IndexedBioSeq]]
    return (new IndexedFastaIterator(in.getLines)).asInstanceOf[Iterator[IndexedBioSeq]]
   }
}

object Fasta {
	val indexedReader = new IndexedFastaReader() 
  
  def write(out:OutputStream,seqs:Iterator[BioSeq],lineLen:Int) {
	  for(seq <- seqs) {
		  out.write('>')
		  out.write(seq.name.getBytes)
		  out.write('\n')
		  val s = seq.text
		  for(i <- 0 to s.length by lineLen) {
		 	  out.write(s.substring(i, (i+lineLen) min (s.length)).getBytes)
		 	  out.write('\n')
		  }
	  }
  }
  
  def write(filename:String,seqs:Iterator[BioSeq],lineLen:Int) {
	   val out=new FileOutputStream(filename)
	   write(out,seqs,lineLen)
	   out.close
  }
  
  def main(args: Array[String]) {
    if (args.length > 0) {
      write(Console.out,
    		  indexedReader.read(Source.fromFile(args(0))),50)
    } else {
      println("Missing arg")
    }
  }
}
