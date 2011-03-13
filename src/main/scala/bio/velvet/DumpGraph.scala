package bio.velvet

import bio.db.GraphDB
import java.io.File
import org.apache.commons.io.FileUtils

object DumpGraph extends VelvetReader {

	def main(args:Array[String]) {
		if(args.length > 0) {
			val input = args(0)
			val dbfile = new File("graphdb")
			if (dbfile.exists) {
				FileUtils.deleteDirectory(dbfile)
			}
			GraphDB.importFile("graphdb", input)
		} else
			println("Not enought arguments")
	}
}