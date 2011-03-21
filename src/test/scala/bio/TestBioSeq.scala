package bio

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import scala.io.Source

class TestBioSeq {
	@Test
	def testCreateSimpleBioSeq {
		val s1:BioSeq = SimpleBioSeq("s1","ACGT")
		assertEquals("s1",s1.name)
		assertEquals("ACGT",s1.text)
		println(s1)
	}
	
	@Test
	def testCreateIndexedBioSeq {
		val s1:BioSeq = IndexedBioSeq("s1","ACGT",1)
		assertEquals("s1",s1.name)
		assertEquals("ACGT",s1.text)
		assertEquals(1,s1.asInstanceOf[IndexedBioSeq].idx)
		println(s1)
	}
	
}