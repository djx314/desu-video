scalaVersion := "2.13.8"

Compile / compile := ((Compile / compile) dependsOn (Compile / scalafmtSbt)).value

libraryDependencies ++= libScalax.`binding.scala`.value

scalacOptions += "-Ymacro-annotations"

enablePlugins(ScalaJSPlugin, ScalaJSWeb)

name    := "frontend"
version := "0.0.1"
