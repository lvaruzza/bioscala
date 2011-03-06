package bio.math

import org.apache.commons.math.special.Gamma

object Binomial {
	def logBinomialProb(p:Double,n:Int,k:Int) = logBinomialCoef(k,n) + k*p + (n-k)*(1.0-p)
		
	def logBinomialCoef(n:Int,k:Int) = Gamma.logGamma(n+1)-Gamma.logGamma(k+1)-Gamma.logGamma(n-k+1) 
}