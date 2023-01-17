import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name       := "desu-video-codegen"
moduleName := "desu-video-codegen"

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:mysql://127.0.0.1:3306/desu_video?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&nullNamePatternMatchesAll=true&useSSL=false"
flywayUser     := "root"
flywayPassword := "root"
flywayLocations += "db/migration"

libraryDependencies ++= Dependencies.mysql
libraryDependencies ++= Dependencies.slick

libraryDependencies ++= Dependencies.quill map (_ exclude ("org.scala-lang.modules", s"scala-java8-compat_${CrossVersion.binaryScalaVersion(scalaVersion.value)}"))
libraryDependencies += Dependencies.scalaReflect(scalaVersion.value)


