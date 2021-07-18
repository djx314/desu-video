import sbt._
import sbt.Keys._
import org.scalax.sbt.CustomSettings
import org.scalax.sbt.Dependencies

CustomSettings.customSettings

// transitiveClassifiers in ThisBuild := Seq("sources", "jar", "javadoc")

resolvers += "Bintary JCenter" at "http://jcenter.bintray.com"

name := "desu-encoder"
version := "0.0.1"

libraryDependencies ++= Dependencies.macwire
libraryDependencies += ws
libraryDependencies += Dependencies.commonsIO
libraryDependencies ++= Dependencies.circe
libraryDependencies ++= Dependencies.zio

enablePlugins(PlayScala, PlayAkkaHttpServer)
disablePlugins(PlayNettyServer)

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

mappings in Universal ++= copyFiles(
  file("./FormatFactory-4.1.0"),
  "FormatFactory-4.1.0"
)
