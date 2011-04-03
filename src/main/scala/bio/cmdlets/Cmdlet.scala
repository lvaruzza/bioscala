package bio.cmdlets


abstract trait Cmdlet {
	def run(args:Array[String])
}