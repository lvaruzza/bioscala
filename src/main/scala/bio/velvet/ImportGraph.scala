package bio.velvet

import bio.db.GraphDB
import java.io.File
import org.apache.commons.io.FileUtils

object ImportGraph extends VelvetReader {

	def main(args:Array[String]) {
		if(args.length > 1) {
			val input = args(0)
			val output = args(1)
			val dbfile = new File(output)
			if (dbfile.exists) {
				FileUtils.deleteDirectory(dbfile)
			}
			GraphDB.importFile(dbfile.getAbsolutePath, input)
		} else
			println("Not enought arguments")
	}
}