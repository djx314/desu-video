import org.scalax.sbt._

CustomSettings.scala213Config

scalafmtOnCompile := true

scalaVersion := scalaV.v213

name := "desu-video-http4s"

libraryDependencies ++= libScalax.`typesafe-config`.value
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= libScalax.`cats-effect`.value
libraryDependencies ++= libScalax.macwire.value
libraryDependencies ++= libScalax.circe.value
libraryDependencies ++= libScalax.zio2.value
libraryDependencies ++= libScalax.`zio-config`.value
libraryDependencies ++= libScalax.doobie.value
libraryDependencies ++= Dependencies.catsCPS
libraryDependencies ++= libScalax.`kind-projector`.value

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "rescarta" at "https://software.rescarta.org/nexus/content/repositories/thirdparty/"
libraryDependencies += "org.tritonus"         % "tritonus-share"     % "0.3.6"
libraryDependencies += "org.tritonus"         % "tritonus-remaining" % "0.3.6"
libraryDependencies += ("com.github.umjammer" % "mp3spi"             % "1.9.8").exclude("org.tritonus", "*")

Assets / pipelineStages := Seq(scalaJSPipeline)
Compile / compile       := ((Compile / compile) dependsOn scalaJSPipeline).value

name    := "http4s"
version := "0.0.1"
