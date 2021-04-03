org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video"

val rootPath    = file(".")
val backendPath = rootPath / "backend"

val common   = project in backendPath / "desu-video-common"
val tapir    = (project in backendPath / "desu-video-tapir").dependsOn(common)
val akkaHttp = (project in backendPath / "desu-video-akka-http").dependsOn(common)
val shouzhi  = (project in rootPath / "desu-shushouzhi")

addCommandAlias("prun", "tapir/reStart")
addCommandAlias("krun", "akkaHttp/run")
