org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig
enablePlugins(SbtTwirl)

name := "grim-dawn"

libraryDependencies ++= org.scalax.sbt.Dependencies.akkaHttp
libraryDependencies ++= org.scalax.sbt.Dependencies.macwire
libraryDependencies ++= org.scalax.sbt.Dependencies.scalatest
libraryDependencies ++= org.scalax.sbt.Dependencies.simpleLogger
