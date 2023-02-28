import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config

scalaVersion := scalaV.v3

scalafmtOnCompile := true

name       := "desu-video-common3"
moduleName := "desu-video-common3"

libraryDependencies ++= Dependencies.quill_scala3 map (_ exclude ("org.scala-lang.modules", s"scala-java8-compat_2.13"))
