package bio.math

import org.junit.runner.RunWith
import org.specs._
import org.specs.matcher._
import org.specs.runner._


@RunWith(classOf[JUnitSuiteRunner])
class BinomislSpecTest extends Specification with JUnit {
	"logBinomial(10,3)" should {
		"be equal to 4.787492" in {
			Binomial.logCoef(10,3) must beCloseTo(4.787492,1e-6)
		}
	}
}