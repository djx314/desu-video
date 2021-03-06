package org.scalax.sbt

import sbt._
import sbt.Keys._

object CustomSettings {

  final private val scalaSettings =
    Seq(
      scalaVersion := "2.13.2"
      , scalacOptions ++= Seq("-feature", "-deprecation"),
      // libraryDependencies += "com.lihaoyi" %% "acyclic" % "0.1.4" % "provided",
      // autoCompilerPlugins := true,
      // addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.4")
      // addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    )

  final private val fmt = org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

  final private val playSettings = {
    import play.sbt.routes.RoutesKeys._

    Seq(
      routesGenerator := InjectedRoutesGenerator
      , com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys.engineType := com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys.EngineType.Trireme
      //TwirlKeys.templateImports ++= Seq("acyclic.file"/*, "scalaz._", "Scalaz._"*/),
      /*com.arpnetworking.sbt.typescript.Import.TypescriptKeys.configFile := {
        "../extras/typescript-ext/tsconfig.json"
      }*/
    )
  }

  final private val assemblyPluginSettings = {
    import sbtassembly.AssemblyKeys._
    import sbtassembly.{MergeStrategy, PathList}

    sbtassembly.AssemblyPlugin.assemblySettings.++(
      Seq(
        mainClass in assembly := Some("play.core.server.ProdServerStart")
        , fullClasspath in assembly += Attributed.blank(
          play.sbt.PlayImport.PlayKeys.playPackageAssets.value
        )
        , assemblyMergeStrategy in assembly := {
          val old = (assemblyMergeStrategy in assembly).value
          s =>
            s match {
              case "reference.conf" => MergeStrategy.concat
              case "application.conf" => MergeStrategy.concat
              case "plugin.properties" => MergeStrategy.concat
              case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.concat
              case PathList("play", "reference-overrides.conf") => MergeStrategy.concat
              case PathList("META-INF", "spring.tooling") => MergeStrategy.discard
              case x => old(x)
            }
        }
      )
    )
  }

  final private val nativePackageSettings = {
    import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
    import com.typesafe.sbt.packager.windows.WindowsPlugin.autoImport._
    import com.typesafe.sbt.SbtNativePackager.autoImport._

    Seq(
      mappings in Windows := (mappings in Universal).value,
      // general package information (can be scoped to Windows)
      maintainer := "djx314<djx314@sina.cn>"
      , packageSummary := "enuma-windows"
      , packageDescription := """Enuma Elish.""",
      // wix build information
      wixProductId in Windows := "ce07be71-510d-414a-92d4-dff47631848a"
      , wixProductUpgradeId in Windows := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"
    )
  }

  val orgSetting = List(version := "0.0.1", organization := "org.scalax.desu")

  final val customSettings = scalaSettings ++ playSettings ++ assemblyPluginSettings ++ nativePackageSettings ++ fmt ++ orgSetting
  final val commonProjectSettings = scalaSettings ++ fmt ++ orgSetting

}
