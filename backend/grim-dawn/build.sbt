import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config

enablePlugins(SbtTwirl)

scalaVersion      := scalaV.v213
scalafmtOnCompile := true
name              := "grim-dawn"

libraryDependencies ++= Dependencies.akkaHttp
libraryDependencies ++= libScalax.macwire.value
libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies += Dependencies.jintellitype
libraryDependencies += Dependencies.thumbnailator
libraryDependencies += Dependencies.javacv
libraryDependencies ++= Dependencies.scalafx
libraryDependencies += Dependencies.distage
libraryDependencies ++= Dependencies.cats2
libraryDependencies += Dependencies.zio
libraryDependencies ++= libScalax.`kind-projector`.value

run / fork := true
