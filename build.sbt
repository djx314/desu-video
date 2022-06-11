org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video"

val rootPath          = file(".")
val backendPath       = rootPath / "backend"
val commonPath        = backendPath / "desu-video-common"
val commonPath3       = backendPath / "desu-video-common3"
val commonPath2       = backendPath / "desu-video-common2"
val commonCodegenPath = backendPath / "desu-video-codegen"

val common   = project in commonPath
val common3  = (project in commonPath3).dependsOn(common)
val common2  = (project in commonPath2).dependsOn(common)
val codegen  = project in commonCodegenPath
val http4s   = (project in backendPath / "desu-video-http4s").dependsOn(common)
val akkaHttp = (project in backendPath / "desu-video-akka-http").dependsOn(common3)
val zio      = (project in backendPath / "desu-video-zio").dependsOn(common2)
val test     = (project in backendPath / "desu-video-test").dependsOn(common3)
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
addCommandAlias(
  "quillCodegen",
  s"codegen/runMain desu.video.common.quill.codegen.MysqlDesuQuillVideoCodegen ${commonPath3.getAbsolutePath}"
)
