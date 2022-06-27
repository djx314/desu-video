import org.scalax.sbt._

CustomSettings.scala3Config
CustomSettings.fmtConfig

name := "desu-video-http4s"

libraryDependencies ++= Dependencies.config
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= Dependencies.http4s
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.circe
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.doobie
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++=Dependencies.catsCPS