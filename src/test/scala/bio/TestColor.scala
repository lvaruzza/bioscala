package bio

/**
 * Created by IntelliJ IDEA.
 * User: varuzzl1
 * Date: 04/07/11
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class TestColor {
  @Test
  def testDecodeFirst() {
    assertEquals(Color.decodeFirst("T01"),"T1")

  }

  @Test
  def testColor2De() {
    assertEquals(Color.color2de("T0123"),"TACGT")
  }
  @Test
  def testDecodeColor() {
    assertEquals(Color.decodeColor("T0123"),"TGAT")
    assertEquals(Color.decodeColor("T2312313121"),"CGTCGTACTG")
  }
}
