org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "node-test"

libraryDependencies ++= org.scalax.sbt.Dependencies.config
libraryDependencies ++= org.scalax.sbt.Dependencies.simpleLogger
libraryDependencies ++= org.scalax.sbt.Dependencies.http4s
libraryDependencies ++= org.scalax.sbt.Dependencies.cats
libraryDependencies ++= org.scalax.sbt.Dependencies.tapir
libraryDependencies += org.scalax.sbt.Dependencies.zioLogging

run / fork := true
