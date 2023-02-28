scalaVersion := scalaV.v213

org.scalax.sbt.CustomSettings.scala213Config

libraryDependencies ++= libScalax.`binding.scala`.value

scalacOptions += "-Ymacro-annotations"

name    := "frontend"
version := "0.0.1"
