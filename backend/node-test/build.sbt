org.scalax.sbt.CustomSettings.scala213Config

scalaVersion      := scalaV.v213
scalafmtOnCompile := true

name := "node-test"

libraryDependencies ++= libScalax.`typesafe-config`.value
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies ++= libScalax.`http4s-Release`.value
libraryDependencies ++= org.scalax.sbt.Dependencies.cats
libraryDependencies ++= org.scalax.sbt.Dependencies.tapir
libraryDependencies += org.scalax.sbt.Dependencies.zioLogging

run / fork := true
