import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-test"
moduleName := "desu-video-test"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies += Dependencies.jintellitype
libraryDependencies ++= Dependencies.circe
libraryDependencies += Dependencies.hikariCP
libraryDependencies ++= Dependencies.tapir

libraryDependencies += Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.quill_scala3
libraryDependencies ++= Dependencies.scalaCollectionCompat