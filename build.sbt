org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video"

val rootPath    = file(".")
val backendPath = rootPath / "backend"

val common   = project in backendPath / "desu-video-common"
val http4s   = (project in backendPath / "desu-video-http4s").dependsOn(common)
val akkaHttp = (project in backendPath / "desu-video-akka-http").dependsOn(common)
val shouzhi  = (project in rootPath / "desu-shushouzhi")

addCommandAlias("prun", "http4s/reStart")
addCommandAlias("krun", "akkaHttp/run")
