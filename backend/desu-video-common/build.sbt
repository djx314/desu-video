import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig
org.scalax.sbt.CustomSettings.crossConfig

name       := "desu-video-common"
moduleName := "desu-video-common"

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:mysql://127.0.0.1:3306/desu_video?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&nullNamePatternMatchesAll=true&useSSL=false"
flywayUser     := "root"
flywayPassword := "root"
flywayLocations += "db/migration"

libraryDependencies ++= Dependencies.mysql
// libraryDependencies ++= Dependencies.slick
// libraryDependencies ++= Dependencies.circe
// libraryDependencies ++= Dependencies.tapir

// libraryDependencies ++= Dependencies.quill map (_ exclude ("org.scala-lang.modules", s"scala-java8-compat_${CrossVersion.binaryScalaVersion(scalaVersion.value)}"))
// libraryDependencies += Dependencies.scalaReflect(scalaVersion.value)
