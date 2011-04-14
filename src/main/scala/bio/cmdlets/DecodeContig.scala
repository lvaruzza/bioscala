package bio.cmdlets

import java.io._
import bio.Color
import bio.velvet._

/*
 * Heuristic for decoding contig from color space to base space
 * 
 */
object DecodeContig extends Cmdlet {
	var debug = false
	
	def maxIdx (cols:Array[Int]) = 
		cols.zipWithIndex.reduceLeft({(acc,x) => if (x._1 > acc._1) x else acc}) 
		
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
	
	def decodeContig(colorSeq:String,displacement:Int,strand:Char,
			baseDensity:Array[Array[Int]],
			colorDensity:Array[Array[Int]]):String = {

      var i = 0
      val extSeq = (" " * displacement) + colorSeq
      var lastBase = ' ';
      var lastColor = -1
      var decodedStr = Array.ofDim[Char](extSeq.length)
      var starts = Array.ofDim[Char](extSeq.length)
      var result = Array.ofDim[Char](extSeq.length)
      var chooses = Array.ofDim[Char](extSeq.length)

      while(i < baseDensity.length) {
    	  val idxStart = maxIdx(baseDensity(i))
    	  val idxColor = maxIdx(colorDensity(i))
    	  val start = (if (idxStart._1 >0) Color.num2base(idxStart._2) else ' ')
    	  val color = idxColor._2
    	  val decoded = if (i!=0 && lastBase != ' ') Color.c2b(lastBase)(Color.num2color(color)) else ' '
    	  
    	  if (debug) println(extSeq(i) + color.toString + start.toString + decoded.toString + ": " + lastBase + " " + baseDensity(i).mkString("\t") + "\t" + colorDensity(i).mkString("\t"))

    	   if (start == ' ') {
    	  	    lastBase=decoded 
    	  	    chooses(i) = '.'
    	   } else if (decoded == ' ') {
    	  	   lastBase = start
    	  	    chooses(i) = '*'
    	   }else {
    	  	   if (start == decoded) {
    	  		   lastBase=decoded
    	  	   } else if (i+1 < baseDensity.length){
    	  		   val nextStartIdx = maxIdx(baseDensity(i+1))
    	  		   val nextStart = (if (nextStartIdx._1 >0) Color.num2base(nextStartIdx._2) else ' ')
    	  		   val nextColor = maxIdx(colorDensity(i+1))
    	  		   val nextDecodedDecoded = Color.c2b(decoded)(Color.num2color(nextColor._2))
    	  		   val nextDecodedStart = Color.c2b(start)(Color.num2color(nextColor._2))
    	  	  	   
    	  		   if (nextDecodedStart == nextStart ) {
    	  		  	   lastBase = start
    	  		  	   chooses(i) = '*'
    	  		  	   
    	  		   } else {
    	  		  	   lastBase = decoded
    	  		  	   chooses(i) = '.'
    	  		   }
    	  	   } else {
    	  	  	   lastBase = decoded
    	  	  	   chooses(i) = '.'
    	  	   }
    	   }  
    	   decodedStr(i)=decoded
    	   starts(i)=start
    	   result(i)=lastBase
    	   lastColor = color
    	   i+=1
      }
      
      if (debug) printResults(
    		  chooses.mkString,
    		  correctStrand(strand,result.mkString),
    		  correctStrand(strand,starts.mkString),
    		  correctStrand(strand,decodedStr.mkString))
    		  
      return result.drop(displacement).mkString
	}
	
	def decodeContigFile(file:File) = {
		val in = new ObjectInputStream(new FileInputStream(file))
		
		val c = in.readObject.asInstanceOf[ContigAlign]
		
		decodeContig(c.colorSeq,c.displacement,c.strand,c.baseDensity,c.colorDensity)
	}
	
	def run(args:Array[String]) {
		if (args.length > 0) {
			val decoded = decodeContigFile(new File(args(0)))
			println(decoded)
		} else {
			println("Invalid number of arguments")
		}
	}
}