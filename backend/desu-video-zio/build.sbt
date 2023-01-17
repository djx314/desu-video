import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "desu-video-zio"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.slick
libraryDependencies ++= Dependencies.scalaCollectionCompat
libraryDependencies += "org.julienrf" %% "play-json-derived-codecs" % "10.0.2"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-tethys" % Dependencies.versions.tapir
libraryDependencies ++= Dependencies.tethysJson

addCompilerPlugin(Dependencies.kindProjector)
