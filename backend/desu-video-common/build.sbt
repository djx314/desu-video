import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig
org.scalax.sbt.CustomSettings.crossConfig

name       := "desu-video-common"
moduleName := "desu-video-common"

libraryDependencies ++= Dependencies.mysql

