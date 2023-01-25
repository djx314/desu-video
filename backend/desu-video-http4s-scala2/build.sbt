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

addCompilerPlugin(Dependencies.kindProjector)

Compile / compile := ((Compile / compile) dependsOn (Compile / scalafmtSbt)).value

enablePlugins(SbtWeb)
Assets / pipelineStages := Seq(scalaJSPipeline)
Compile / compile       := ((Compile / compile) dependsOn scalaJSPipeline).value

name    := "http4s"
version := "0.0.1"
