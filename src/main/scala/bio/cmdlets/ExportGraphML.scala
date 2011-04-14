package bio.cmdlets

import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter
import java.io._

object ExportGraphML extends Cmdlet {
  def exportGraphML(inputName: String, outputName: String) {
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
  }

  def run(args: Array[String]) {
    if (args.length > 1) {
    	exportGraphML(args(0),args(1))
    } else
      println("Not enought arguments")
      println("Use exportGraphML graphDbDir outputXMLFile")
  }
}
