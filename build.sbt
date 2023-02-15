org.scalax.sbt.CustomSettings.scala213Config
org.scalax.sbt.CustomSettings.fmtConfig

scalaVersion := scalaV.v213

name := "desu-video"

val rootPath          = file(".").getCanonicalFile
val backendPath       = rootPath / "backend"
val frontendPath      = rootPath / "frontend"
val commonPath        = backendPath / "desu-video-common"
val commonPath3       = backendPath / "desu-video-common3"
val commonPath2       = backendPath / "desu-video-common2"
val commonCodegenPath = backendPath / "desu-video-codegen"

val root             = project in rootPath
val common           = project in commonPath
val common3          = (project in commonPath3).dependsOn(common)
val common2          = (project in commonPath2).dependsOn(common)
val codegen          = (project in commonCodegenPath).dependsOn(common)
lazy val http4s      = (project in backendPath / "desu-video-http4s-scala2").dependsOn(common).settings(scalaJSProjects := Seq(frontendJS))
val akkaHttp         = (project in backendPath / "desu-video-akka-http").dependsOn(common3)
val zio              = (project in backendPath / "desu-video-zio").dependsOn(common2)
val test             = (project in backendPath / "desu-video-test").dependsOn(common3)
val nodeTest         = project in backendPath / "node-test"
val gd               = project in backendPath / "grim-dawn"
val finch            = project in backendPath / "desu-video-finch"
lazy val frontend    = crossProject(JSPlatform, JVMPlatform) in frontendPath
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
  .partialInput(s" ${commonPath2.getAbsolutePath}")
  .evaluated

val quillCodegen = inputKey[Unit]("quillCodegen")
quillCodegen := (codegen / Compile / runMain)
  .partialInput(s" desu.video.common.quill.codegen.MysqlDesuQuillVideoCodegen")
  .partialInput(s" ${commonPath3.getAbsolutePath}")
  .evaluated

ThisBuild / djxScalafmtFile := rootPath / ".scalafmt_desu-video.conf"
ThisBuild / djxBuildSbtFile := rootPath / "project" / "build.properties"

enablePlugins(ScalaJSPlugin)

Global / onChangedBuildSource := ReloadOnSourceChanges

resolvers += "umjammer" at "https://maven.pkg.github.com/umjammer/mp3spi"
