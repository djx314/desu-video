import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-akka-http"

libraryDependencies ++= Dependencies.akkaHttp
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies += Dependencies.jintellitype
