org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video"

val rootPath    = file(".")
val backendPath = rootPath / "backend"
val commonPath  = backendPath / "desu-video-common"

val common   = project in commonPath
val http4s   = (project in backendPath / "desu-video-http4s").dependsOn(common)
val akkaHttp = (project in backendPath / "desu-video-akka-http").dependsOn(common)
val zio      = project in backendPath / "desu-video-zio"
val nodeTest = project in backendPath / "node-test"
val gd       = project in backendPath / "grim-dawn"
val finch    = project in backendPath / "desu-video-finch"

addCommandAlias("prun", "http4s/reStart")
addCommandAlias("krun", "akkaHttp/run")
addCommandAlias("grun", "gd/run")
addCommandAlias("zrun", "zio/reStart")
addCommandAlias("frun", "finch/reStart")
addCommandAlias("flyway", "common/flywayMigrate")

addCommandAlias("slickCodegen", s"common/runMain desu.video.common.slick.codegen.MysqlDesuVideoCodegen ${commonPath.getAbsolutePath}")
addCommandAlias("quillCodegen", s"common/runMain desu.video.common.quill.codegen.MysqlDesuQuillVideoCodegen ${commonPath.getAbsolutePath}")
