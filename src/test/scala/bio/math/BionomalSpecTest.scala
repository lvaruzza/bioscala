package bio.math

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner._
import org.junit.Test

@Test
@RunWith(classOf[JUnitSuiteRunner])
class BionomalSpecTest extends Specification with JUnit {
	"logBinomialCoef(10,3)" should {
		"be equal to 4.787492" in {
			Binomial.logBinomialCoef(10,3) must beCloseTo(4.787492,1e-6)
		}
	}
	
	"logBinomialProb(10,3)" should {
		"be equal to -0.2537637" in {
			Binomial.logBinomialProb(0.25,10,3) must beCloseTo(-1.385166,1e-6)
		}
	}
	
}


