import scala.sys.process._
import scala.language.postfixOps
import java.net.URL
import java.io.File

val path = "/Users/masunghoon/Dropbox/programmingScala2"
val src = s"$path/src"
val resources = s"$src/main/resources"

s"ls -l $src".!

Seq("ls", "-l", src).!!

// Build a process to open a URL, redirect the output to "grep $filter",
// and append the output to file (not overwrite it).
def findURL(url: String, filter: String) =
  new URL(url) #> s"grep $filter" #>> new File(s"$resources/$filter.txt")

// Run ls -l on the file. If it exists, then count the lines.
def countLines(fileName: String) =
  s"ls -l $resources/$fileName" #&& s"wc -l $resources/$fileName"

// findURL("http://scala-lang.org", "scala") !
// countLines("scala.txt") !

// findURL("http://scala-lang.org", "scala") !
// countLines("scala.txt") !
