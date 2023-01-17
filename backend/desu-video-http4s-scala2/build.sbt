import org.scalax.sbt._

CustomSettings.scala213Config
CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "desu-video-http4s"

libraryDependencies ++= Dependencies.config
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.circe
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.doobie
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++=Dependencies.catsCPS

addCompilerPlugin(Dependencies.kindProjector)
