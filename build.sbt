org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video"

val common   = project in file(".") / "backend" / "common"
val tapir    = (project in file(".") / "backend" / "tapir").dependsOn(common)
val akkaHttp = (project in file(".") / "backend" / "akka-http").dependsOn(common)
