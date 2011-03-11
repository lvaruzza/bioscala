package bio.math

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner._
import org.junit.Test

@Test
@RunWith(classOf[JUnitSuiteRunner])
class BionomalSpecTest extends Specification with JUnit {
	"logBinomialCoef" should {
		"(10,3) be equal to 4.787492" in {
			Binomial.logCoef(10,3) must beCloseTo(4.787492,1e-6)
		} 
		"(400,130) be equal to 4.787492" in {
			Binomial.logCoef(400,130) must beCloseTo(249.0755,1e-4)
		}
	}
	
	
	"logProb" should {
		"(0.9,0,0) be equal to 0" in {
			Binomial.logProb(0.9,0,0) must beCloseTo( 0,1e-6)
		}
		"(0.9,2,2) be equal to  -0.2107210" in {
			Binomial.logProb(0.9,2,2) must beCloseTo( -0.2107210,1e-6)
		}
		"(0.25,10,3) be equal to -0.2537637" in {
			Binomial.logProb(0.25,10,3) must beCloseTo(-1.385166,1e-6)
		}
	}
	
}


