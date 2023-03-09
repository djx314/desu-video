scalaVersion := scalaV.v3

org.scalax.sbt.CustomSettings.scala3Config

libraryDependencies ++= libScalax.`binding.scala`.value

name    := "frontend"
version := "0.0.1"

scalafmtOnCompile := true
