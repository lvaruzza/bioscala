package bio.math

import org.apache.commons.math.special.Gamma

object Binomial {
	def logBinomialProb(p:Double,k:Int,n:Int) = logBinomialCoef(k,n) + k*p + (n-k)*(1.0-p)
		
	def logBinomialCoef(k:Int,n:Int) = Gamma.logGamma(n+1)-Gamma.logGamma(k+1)-Gamma.logGamma(n-k+1) 
}