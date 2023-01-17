org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "node-test"

libraryDependencies ++= org.scalax.sbt.Dependencies.config
libraryDependencies ++= org.scalax.sbt.Dependencies.simpleLogger
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= org.scalax.sbt.Dependencies.cats
libraryDependencies ++= org.scalax.sbt.Dependencies.tapir
libraryDependencies += org.scalax.sbt.Dependencies.zioLogging

run / fork := true
