import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config

scalaVersion := scalaV.v3

name              := "desu-video-akka-http"
moduleName        := "desu-video-akka-http"
scalafmtOnCompile := true

libraryDependencies ++= Dependencies.akkaHttp
  .map(_ cross CrossVersion.for3Use2_13)
  .map(_ exclude ("io.circe", "*"))
  .map(_ exclude ("dev.zio", "*"))
  .map(_ exclude ("org.scala-lang.modules", "scala-java8-compat_2.13"))
  .map(_ exclude ("org.scala-lang.modules", "scala-collection-compat_2.13"))
libraryDependencies ++= libScalax.macwire.value
libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies ++= Dependencies.zioJson
libraryDependencies += Dependencies.hikariCP
