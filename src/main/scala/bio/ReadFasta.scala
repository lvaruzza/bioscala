import scala.io.Source
import scala.annotation.tailrec
import java.io.File
import java.io.InputStream

package bio {
  case class BioSeq(val name: String, val text: String,val idx:Int)

  class BioSeqIterator(lines: Iterator[String]) extends Iterator[BioSeq] {
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

      val seq = new BioSeq(header.stripPrefix(">"), sb.toString,idx)
      idx += 1
      seq
    }

    def hasNext = lines.hasNext
  }

  object ReadFasta {

    def read(in: Source): Iterator[BioSeq] = {
      return new BioSeqIterator(in.getLines)
    }
    
    def read(in: File): Iterator[BioSeq] = {
    	return read(Source.fromFile(in))
    }

    def read(in: InputStream): Iterator[BioSeq] = {
    	return read(Source.fromInputStream(in))
    }

    def main(args: Array[String]) {
      if (args.length > 0) {
        val seqs = read(Source.fromFile(args(0)))
        for (seq <- seqs) println(seq)
      } else {
        println("Missing arg")
      }
    }
  }
}
