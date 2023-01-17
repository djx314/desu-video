import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name       := "desu-video-common2"
moduleName := "desu-video-common2"

libraryDependencies ++= Dependencies.slick

addCompilerPlugin(Dependencies.kindProjector)
