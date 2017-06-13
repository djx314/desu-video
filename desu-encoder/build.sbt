import sbt._
import sbt.Keys._

val printlnDo = println("""
|   __     __     __
|  / /    / /    / /
| / /_   / /_   / /_
|| '_ \ | '_ \ | '_ \
|| (_) || (_) || (_) |
| \___/  \___/  \___/
""".stripMargin
)

lazy val playVersion = play.core.PlayVersion.current

transitiveClassifiers in ThisBuild := Seq("sources", "jar", "javadoc")

name := "desu-encoder"
version := "0.0.1"

libraryDependencies += guice

enablePlugins(play.sbt.PlayScala, PlayAkkaHttpServer)
  disablePlugins(PlayNettyServer)

addCommandAlias("erun", "encoder/run 2333")

def copyFiles(root: File, prefix: String): List[(File, String)] = {
  println(root.getCanonicalPath)
  root.listFiles().toList.flatMap { file =>
    if (file.isDirectory) {
      copyFiles(file, prefix + "/" + file.getName)
    } else {
      List(file -> (prefix + "/" + file.getName))
    }
  }
}

mappings in Universal ++= copyFiles(file("./FormatFactory-4.1.0"), "FormatFactory-4.1.0")