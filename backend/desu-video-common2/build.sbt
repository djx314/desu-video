import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config

scalaVersion      := scalaV.v213
scalafmtOnCompile := true

name       := "desu-video-common2"
moduleName := "desu-video-common2"

libraryDependencies ++= Dependencies.slick

addCompilerPlugin(Dependencies.kindProjector)
