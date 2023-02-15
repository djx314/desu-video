import org.scalax.sbt._

CustomSettings.scala213Config
CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "desu-video-http4s"

libraryDependencies ++= libScalax.`typesafe-config`.value
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= libScalax.circe.value
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.doobie
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.catsCPS

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "rescarta" at "https://software.rescarta.org/nexus/content/repositories/thirdparty/"
libraryDependencies += "org.tritonus"         % "tritonus-share"     % "0.3.6"
libraryDependencies += "org.tritonus"         % "tritonus-remaining" % "0.3.6"
libraryDependencies += ("com.github.umjammer" % "mp3spi"             % "1.9.8").exclude("org.tritonus", "*")

addCompilerPlugin(Dependencies.kindProjector)

Compile / compile := ((Compile / compile) dependsOn (Compile / scalafmtSbt)).value

enablePlugins(SbtWeb)
Assets / pipelineStages := Seq(scalaJSPipeline)
Compile / compile       := ((Compile / compile) dependsOn scalaJSPipeline).value

name    := "http4s"
version := "0.0.1"

val bb = inputKey[Unit]("hh")
bb := {
  reStart / mainClass := Option("desu.mainapp.AbcAppRun")
  (Compile / reStart).inputTaskValue.evaluated
}
