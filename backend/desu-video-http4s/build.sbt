import org.scalax.sbt._

CustomSettings.scalaConfig
CustomSettings.fmtConfig

name := "desu-video-http4s"

libraryDependencies ++= Dependencies.config
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= Dependencies.http4s
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.macwire
