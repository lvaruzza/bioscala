package bio.velvet

import java.io._
import bio.Color

object DecodeContig {
	def maxIdx (cols:Array[Int]) = 
		cols.zipWithIndex.reduceLeft({(acc,x) => if (x._1 > acc._1) x else acc}) 
		
	def decodeContig(colorSeq:String,displacement:Int,
			baseDensity:Array[Array[Int]],
			colorDensity:Array[Array[Int]]) {

      var i = 0
      val extSeq = (" " * displacement) + colorSeq
      var lastBase = ' ';
      var lastColor = -1
      var decodedStr = Array.ofDim[Char](extSeq.length)
      var starts = Array.ofDim[Char](extSeq.length)
      var result = Array.ofDim[Char](extSeq.length)
      
      while(i < baseDensity.length) {
    	  val idxBase = maxIdx(baseDensity(i))
    	  val idxColor = maxIdx(colorDensity(i))
    	  val base = (if (idxBase._1 >0) Color.num2base(idxBase._2) else ' ')
    	  val color = idxColor._2
    	  val decoded = if (i!=0 && lastBase != ' ') Color.c2b(lastBase)(Color.num2color(color)) else ' '
    	  
    	  println(extSeq(i) + color.toString + base.toString + decoded.toString + ": " + lastBase + " " + baseDensity(i).mkString("\t") + "\t" + colorDensity(i).mkString("\t"))

    	   lastBase = if (base == ' ') {
    	  	    decoded 
    	   } else {
    	  	   //if (base != decoded) {    	 
    	  		  //println("Conflict Detected : base = " + base +  " (%d)".format(idxBase._1) +  
    	  		  //		" decoded = " + decoded + "(%d)".format(idxColor._1))
    	  	   //}
    	  	  base
    	   }  
    	   decodedStr(i)=decoded
    	   starts(i)=base
    	   result(i)=lastBase
    	   lastColor = color
    	   i+=1
      }
	      
      println("r: " + result.mkString)
      println("s: " + starts.mkString)
      println("d: " + decodedStr.mkString)
      print("x: ")
      for(i <- Range(0,result.length)) {
    	  print(if ((starts(i) != ' ') &&  (starts(i) != decodedStr(i))) 'X' else ' ')
      }
      println
	}
	
	def decodeContigFile(file:File) {
		val in = new ObjectInputStream(new FileInputStream(file))
		
		val c = in.readObject.asInstanceOf[ContigAlign]
		
		decodeContig(c.colorSeq,c.displacement,c.baseDensity,c.colorDensity)
	}
	
	def main(args:Array[String]) {
		if (args.length > 0) {
			decodeContigFile(new File(args(0)))
		} else {
			println("Invalid number of arguments")
		}
	}
}