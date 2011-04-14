package bio.cmdlets

import bio.db.GraphDB
import java.io.File
import org.apache.commons.io.FileUtils
import bio.velvet._

object ImportGraph extends VelvetReader with Cmdlet {

  def importGraph(inputFile: String, outputDir: String) {
    val dbfile = getOutput(outputDir)
    if (dbfile.exists) {
      FileUtils.deleteDirectory(dbfile)
    }
    GraphDB.importFile(dbfile.getAbsolutePath, inputFile)
  }

  def run(args: Array[String]) {
    if (args.length > 1) {
    	importGraph(args(0),args(1))
    } else
      println("ERROR: Not enought arguments")
    println("Use ImportGraph graphFile outputDir")
  }
}
