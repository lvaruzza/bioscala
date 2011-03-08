package bio.math

import org.apache.commons.math.special.Gamma

object Binomial {
	def logProb(p:Double,n:Int,k:Int) = {
		if (k>n) error("Binomial Probability error: k (=%d) should not be greater than n (=%d)".format(k,n))
		if (n==0) 0 else logCoef(n,k) + k*Math.log(p) + (n-k)*Math.log(1.0-p)
	}
		//logBinomialCoef(k,n) + k*Math.log(p) + (n-k)*Math.log(1.0-p)
		
	def logCoef(n:Int,k:Int) = Gamma.logGamma(n+1)-Gamma.logGamma(k+1)-Gamma.logGamma(n-k+1) 
 }
