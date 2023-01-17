import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "desu-video-finch"

libraryDependencies ++= Dependencies.finch

run / fork := true

addCompilerPlugin(Dependencies.kindProjector)
