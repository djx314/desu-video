import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config

scalaVersion      := scalaV.v213
scalafmtOnCompile := true
name              := "desu-video-finch"

libraryDependencies ++= Dependencies.finch

run / fork := true

addCompilerPlugin(Dependencies.kindProjector)
