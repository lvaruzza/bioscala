package bio.velvet

import java.io.File
import org.apache.commons.io.FileUtils
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter
import java.io._

object ExportGraphML {

  def main(args: Array[String]) {
    if (args.length > 1) {
      val inputName = args(0)
      val outputName = args(1)
      println("Reading the Graph")
	  val graph = new Neo4jGraph(inputName)
      try {
	      println("Writing the GraphML file " + outputName)
	      val output = new FileOutputStream(outputName)
	      GraphMLWriter.outputGraph(graph, output)
	      output.close()
      } finally {
    	  graph.shutdown()
    	  println("Finish")
      }
    } else
      println("Not enought arguments")
  }
}
