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

resolvers += "Bintary JCenter" at "http://jcenter.bintray.com"

name := "desu-encoder"
version := "0.0.1"

libraryDependencies ++= Seq(
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0"
)

//libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.2"

libraryDependencies += "commons-io" % "commons-io" % "2.5"
libraryDependencies += "play-circe" %% "play-circe" % "2.6-0.8.0"

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