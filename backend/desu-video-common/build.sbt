import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.crossConfig

scalaVersion      := scalaV.v213
scalafmtOnCompile := true

name       := "desu-video-common"
moduleName := "desu-video-common"

libraryDependencies ++= Dependencies.mysql
