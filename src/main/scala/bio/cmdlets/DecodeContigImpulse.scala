package bio.cmdlets

import java.io._
import bio.Color
import bio.math.Binomial
import bio.velvet._

/*
 * Heuristic2 for decoding contig from color space to base space
 * 
 */
object DecodeContigImpulse extends Cmdlet {
	
	var debug  = false
	
	def maxIdx (cols:Array[Int]) = 
		cols.zipWithIndex.reduceLeft({(acc,x) => if (x._1 > acc._1) x else acc}) 

	def maxProbBase(p:Double,c:Array[Int]) {
		val x = maxIdx(c)
		val sum = c.sum
		val logProb = Binomial.logProb(p,x._1,sum) 
	}
	
	def printResults(chooses:String,result:String,starts:String,decoded:String) {
      println("c: " + chooses)
      println("r: " + result)
      println("s: " + starts)
      println("d: " + decoded)
      print("x: ")
      for(i <- Range(0,result.length)) {
    	  print(if ((starts(i) != ' ') &&  (starts(i) != decoded(i))) 'X' else ' ')
      }
      
      println		
	}
	
	
	def correctStrand(strand:Char,str:String) = {
		if (strand == '+')
			str
		else {
			str.reverse.map(Color.revcomp)
		}
	}
	
	private val ArbitraryBaseProb = 0.9
	
	def decodeContig(colorSeq:String,displacement:Int,strand:Char,
			baseDensity:Array[Array[Int]],
			colorDensity:Array[Array[Int]]) {

      var i = 0
      val extSeq = (" " * displacement) + colorSeq
      var lastBase = ' ';
      var lastColor = -1
      var decodedStr = Array.ofDim[Char](extSeq.length)
      var starts = Array.ofDim[Char](extSeq.length)
      var result = Array.ofDim[Char](extSeq.length)
      var chooses = Array.ofDim[Char](extSeq.length)
      var lastProb = 0.0
      
      val probLog = new PrintStream(new FileOutputStream("probs.txt"))
      
      while(i < baseDensity.length) {
    	  val idxBase = maxIdx(baseDensity(i))    	  
    	  val idxColor = maxIdx(colorDensity(i))
    	  val base = (if (idxBase._1 >0) Color.num2base(idxBase._2) else ' ')
    	  val color = idxColor._2
    	  val decoded = if (i!=0 && lastBase != ' ') Color.c2b(lastBase)(Color.num2color(color)) else ' '
    
    	  if (debug) println("lastProb = %.3e k=%d n=%d P(d)=%.3e".format(lastProb,colorDensity(i).sum,idxColor._1,
    	 		  				Binomial.logProb(0.9, colorDensity(i).sum,idxColor._1)))
    	  
    	  val probStart = if(base == ' ') 0 else 0.5
    	                  
    	  val probDecoded = if (lastProb == 0.5) 0.9 else 0.9*lastProb
    	  
    	  if (debug) println(extSeq(i) + color.toString + base.toString + decoded.toString + ": " + lastBase + " | " + baseDensity(i).mkString("\t") + "|\t" + colorDensity(i).mkString("\t"))

    	  
          if (idxColor._1 == 0 || probStart > probDecoded) {
        	  lastBase = base
        	  lastProb = probStart
        	  chooses(i) = '*'
          } else {
        	  lastBase = decoded
        	  lastProb = probDecoded
        	  chooses(i) = '.'
          }
    	   
          if (debug) println("P(start) = %.3e  P(decoded) = %.3e lastProb = %.3e".format(probStart,probDecoded,lastProb))
    	  
          probLog.println("%d\t%.3e\t%.3e".format(i,probStart,probDecoded))
          
    	   decodedStr(i)=decoded
    	   starts(i)=base
    	   result(i)=lastBase
    	   lastColor = color
    	   i+=1
      }
      probLog.close
      
      if (debug) printResults(
    		  		chooses.mkString,
    		  		correctStrand(strand,result.mkString),
    		  		correctStrand(strand,starts.mkString),
    		  		correctStrand(strand,decodedStr.mkString))
	}
	
	def decodeContigFile(file:File) {
		val in = new ObjectInputStream(new FileInputStream(file))
		
		val c = in.readObject.asInstanceOf[ContigAlign]
		
		decodeContig(c.colorSeq,c.displacement,c.strand,c.baseDensity,c.colorDensity)
	}
	
	def run(args:Array[String]) {
		if (args.length > 0) {
			decodeContigFile(new File(args(0)))
		} else {
			println("Invalid number of arguments")
		}
	}
}


