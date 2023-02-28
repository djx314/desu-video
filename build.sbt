org.scalax.sbt.CustomSettings.scala213Config

scalaVersion := scalaV.v213

scalafmtOnCompile := true

name := "desu-video"

val `root/file`     = file(".").getCanonicalFile
val `backend/file`  = `root/file` / "backend"
val `frontend/file` = `root/file` / "frontend"

val `common/file` = `backend/file` / "desu-video-common"
val common        = project in `common/file`

val `common3/file` = `backend/file` / "desu-video-common3"
val common3        = (project in `common3/file`).dependsOn(common)

val `common2/file` = `backend/file` / "desu-video-common2"
val common2        = (project in `common2/file`).dependsOn(common)

val `codegen/file` = `backend/file` / "desu-video-codegen"
val codegen        = (project in `codegen/file`).dependsOn(common)

lazy val http4s   = (project in `backend/file` / "desu-video-http4s-scala2").dependsOn(common).settings(scalaJSProjects := Seq(frontendJS))
val akkaHttp      = (project in `backend/file` / "desu-video-akka-http").dependsOn(common3)
val zio           = (project in `backend/file` / "desu-video-zio").dependsOn(common2)
val test          = (project in `backend/file` / "desu-video-test").dependsOn(common3)
val nodeTest      = project in `backend/file` / "node-test"
val gd            = project in `backend/file` / "grim-dawn"
val finch         = project in `backend/file` / "desu-video-finch"
lazy val frontend = crossProject(JSPlatform, JVMPlatform) in `frontend/file`
lazy val frontendJS  = frontend.js
lazy val frontendJVM = frontend.jvm

val grun = inputKey[Unit]("grun")
grun := (gd / Compile / run).evaluated

val flyway = taskKey[Unit]("flyway")
flyway := (codegen / Compile / flywayMigrate).value

val zrun = inputKey[Unit]("zrun")
zrun := (zio / Compile / reStart).evaluated

val prun = inputKey[Unit]("prun")
prun := (http4s / Compile / reStart).evaluated

val frun = inputKey[Unit]("frun")
frun := (finch / Compile / reStart).evaluated

val krun = inputKey[Unit]("krun")
krun := (akkaHttp / Compile / run).evaluated

val slickCodegen = inputKey[Unit]("slickCodegen")
slickCodegen := (codegen / Compile / runMain)
  .partialInput(s" desu.video.common.slick.codegen.MysqlDesuVideoCodegen")
  .partialInput(s" ${`common2/file`.getAbsolutePath}")
  .evaluated

val quillCodegen = inputKey[Unit]("quillCodegen")
quillCodegen := (codegen / Compile / runMain)
  .partialInput(s" desu.video.common.quill.codegen.MysqlDesuQuillVideoCodegen")
  .partialInput(s" ${`common3/file`.getAbsolutePath}")
  .evaluated

Global / onChangedBuildSource := ReloadOnSourceChanges
