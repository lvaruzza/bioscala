package bio.math

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import bio._
import scala.io.Source

class TestReadFasta extends JUnitSuite {

	val expected = List(BioSeq("1","ACGT",1),BioSeq("2","ACGT",2))
	
	def readFromStringAndTest(expected:List[BioSeq],input:String) {
	  val it = ReadFasta.read(Source.fromString(input))
	  assertEquals(expected,it.toList)		
	}
	
  @Test
  def testReadFastaOk() {
	  readFromStringAndTest(expected,
""">1
ACGT
>2
A
C

G
T""")
  }
  
  @Test
  def testReadFastaWithHeader() {
	  readFromStringAndTest(expected,
	 		  "#Header\n" +
	 		  "#\n" +
	 		  ">1\n" +
	 		  "ACGT\n" + 
	 		  ">2\n" +
	 		  "ACGT")
  }
  
}