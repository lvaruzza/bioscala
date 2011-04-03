package bio.cli

import bio.cmdlets.Cmdlet

object RunCmdlet {
  def run(cmd: String, args: Array[String]) {
    val className = "bio.cmdlets." + cmd
    println("Running class " + className)
    val cmdlet = Class.forName(className).newInstance.asInstanceOf[Cmdlet]
    cmdlet.run(args)
  }

  def main(args: Array[String]) {
    run(args.head, args.tail)
  }
}