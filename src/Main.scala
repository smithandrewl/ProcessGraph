import java.io.File

// Outputs a graph of the linux process tree in GraphViz format
object Main extends App {
  // Builds and returns a graph of the linux process tree
  //
  // The returned format is an array of edges from  a parent process
  // to a child process.
  def getProcessGraph: Array[Pair[String, String]] = {
    // Returns an array of the ids of all running processes in String format
    // This is done by parsing the proc pseudo filesystem (selecting each folder in the /proc directory with an all numeric
    // name).
    def pids: Array[String] = {
      def isNumericDirectory(file: File): Boolean = {
        def isNumeric(str: String): Boolean = {
          str.forall(_.isDigit)
        }

        file.isDirectory && isNumeric(file.getName)
      }

      val filesInProc = new File("/proc").listFiles()
      val numericDirs = filesInProc.filter(isNumericDirectory)

      numericDirs.map(_.getName)
    }

    // Returns a pair containing the parent process id and the process id given a process id
    // This pair represents an edge in the process graph
    def processInfo(pid: String): Pair[String, String] = {
      val PPID_FIELD = 3
      val path       = s"/proc/${pid}/stat"
      val line       = scala.io.Source.fromFile(path).getLines().next()

      Pair(line.split(" ")(PPID_FIELD), pid)
    }

    pids.map(processInfo)
  }

  // ------ Main ------------
  println("digraph processGraph {")

  getProcessGraph.foreach {
    process => println(s"  ${process._1} -> ${process._2}")
  }

  println("}")
}
