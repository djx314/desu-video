import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config

scalaVersion      := scalaV.v213
scalafmtOnCompile := true

name := "desu-video-zio"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.slick
libraryDependencies ++= libScalax.`scala-collection-compat`.value
libraryDependencies += "org.julienrf"                %% "play-json-derived-codecs" % "10.0.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-tethys"        % Dependencies.versions.tapir
libraryDependencies ++= Dependencies.tethysJson

libraryDependencies ++= libScalax.`kind-projector`.value
