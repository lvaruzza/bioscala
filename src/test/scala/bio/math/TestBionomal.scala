package bio.math

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class TestTest extends JUnitSuite  {
  @Test
  def testBinomial() {
    assertEquals(Binomial.logCoef(10, 3), 4.787492, 1e-6)
    assertEquals(Binomial.logCoef(400, 130), 249.0755, 1e-4)
  }

  @Test
  def testLogProb() {
    assertEquals(Binomial.logProb(0.9, 0, 0), 0, 1e-6)
    assertEquals(Binomial.logProb(0.9, 2, 2), -0.2107210, 1e-6)
    assertEquals(Binomial.logProb(0.25, 10, 3), -1.385166, 1e-6)
  }
}