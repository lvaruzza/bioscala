package bio

import scala.collection.immutable.Map

object Color {
	val de2c = Map('A'->'0',
				   'C'->'1',
				   'G'->'2',
				   'T'->'3',
				   'a'->'0',
				   'c'->'1',
				   'g'->'2',
				   't'->'3')
				
	def de2color(de:String):String = {
		de.map(c => de2c(c))
	}
}