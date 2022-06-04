import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-common3"
moduleName := "desu-video-common3"

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:mysql://127.0.0.1:3306/desu_video?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&nullNamePatternMatchesAll=true&useSSL=false"
flywayUser     := "root"
flywayPassword := "root"
flywayLocations += "db/migration"

libraryDependencies ++= Dependencies.mysql
libraryDependencies ++= Dependencies.quill_scala3 map (_ exclude ("org.scala-lang.modules", s"scala-java8-compat_${CrossVersion.binaryScalaVersion(scalaVersion.value)}"))
libraryDependencies ++= Dependencies.circe
