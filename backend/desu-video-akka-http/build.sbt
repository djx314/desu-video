import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-akka-http"
moduleName := "desu-video-akka-http"

libraryDependencies ++= Dependencies.akkaHttp.map(_ cross CrossVersion.for3Use2_13).map(_ exclude("io.circe", "*"))
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies += Dependencies.jintellitype
libraryDependencies ++= Dependencies.circe
