package bio.io

import scala.io.Source
import scala.annotation.tailrec
import bio.BioSeq

import java.io.OutputStream
import java.io.File
import java.io.InputStream


class FastaIterator(lines: Iterator[String]) extends Iterator[BioSeq] {
  private var lastLine = ""
  private var idx = 1

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

    val seq = new BioSeq(header.stripPrefix(">"), sb.toString, idx)
    idx += 1
    seq
  }

  def hasNext = lines.hasNext
}

object Fasta {

  def read(in: Source): Iterator[BioSeq] = {
    return new FastaIterator(in.getLines)
  }

  def read(in: File): Iterator[BioSeq] = {
    return read(Source.fromFile(in))
  }

  def read(in: InputStream): Iterator[BioSeq] = {
    return read(Source.fromInputStream(in))
  }

  def write(out:OutputStream,seqs:Iterator[BioSeq],lineLen:Int = 100) {
	  for(seq <- seqs) {
		  out.write('>')
		  out.write(seq.name.getBytes)
		  out.write('\n')
		  val s = seq.text
		  for(i <- 0 until s.length by lineLen) {
		 	  out.write(s.substring(i, i+lineLen).getBytes)
		 	  out.write('\n')
		  }
	  }
  }
  
  def main(args: Array[String]) {
    if (args.length > 0) {
      write(Console.out,read(Source.fromFile(args(0))))
    } else {
      println("Missing arg")
    }
  }
}
