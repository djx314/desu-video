import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig
enablePlugins(SbtTwirl)

name := "grim-dawn"

libraryDependencies ++= Dependencies.akkaHttp
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies += Dependencies.jintellitype
libraryDependencies += Dependencies.thumbnailator
libraryDependencies += Dependencies.javacv
libraryDependencies ++= Dependencies.scalafx
libraryDependencies += Dependencies.distage
libraryDependencies ++= Dependencies.cats2
libraryDependencies += Dependencies.zio

addCompilerPlugin(Dependencies.kindProjector)

run / fork := true
