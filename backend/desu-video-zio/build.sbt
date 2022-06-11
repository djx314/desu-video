import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-zio"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies += Dependencies.javacv
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.scalaCollectionCompat

run / fork := true
