package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val AkkaVersion           = "2.6.17"
  val AkkaHttpVersion       = "10.2.7"
  val akkaHttpCirceVersion  = "1.38.2"
  val slf4jVersion          = "1.7.32"
  val typeSafeConfigVersion = "1.4.1"
  val scalatestVersion      = "3.2.9"
  val macwireVersion        = "2.5.0"
  val circeVersion          = "0.15.0-M1"
  val http4sVersion         = "0.23.7"
  val slickVersion          = "3.3.3"
  val mysqlVersion          = "8.0.26"
  val tapirVersion          = "0.19.1"
  val jintellitypeVersion   = "1.4.0"
  val catsEffectVersion     = "3.2.9"
  val sttpVersion           = "3.3.18"
  val kindProjectorVersion  = "0.13.2"
  val quillVersion          = "3.8.0"
  val distageVersion        = "1.0.8"
  val zioLoggingVersion     = "0.5.14"
  val catsEffect2Version    = "2.5.4"
  val zioVersion            = "1.0.9"

  val config = List("com.typesafe" % "config" % typeSafeConfigVersion)

  val simpleLogger = List("org.slf4j" % "slf4j-simple" % slf4jVersion)

  val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-actor-typed"    % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream"         % AkkaVersion,
    "com.typesafe.akka" %% "akka-http"           % AkkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-circe"     % akkaHttpCirceVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit"   % AkkaHttpVersion
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )

  val cats  = Seq("org.typelevel" %% "cats-effect" % catsEffectVersion)
  val cats2 = Seq("org.typelevel" %% "cats-effect" % catsEffect2Version)

  val circe = Seq(
    "io.circe" %% "circe-core"    % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser"  % circeVersion
  )

  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros"     % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "macrosakka" % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "util"       % macwireVersion,
    "com.softwaremill.macwire" %% "proxy"      % macwireVersion
  )

  val scalatest = Seq(
    "org.scalactic" %% "scalactic" % scalatestVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  )

  val mysql = Seq("mysql" % "mysql-connector-java" % mysqlVersion)

  val slick = Seq(
    "com.typesafe.slick" %% "slick"          % slickVersion,
    "com.typesafe.slick" %% "slick-codegen"  % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  )

  val sttp = List(
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio"  % sttpVersion,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpVersion
  )

  val zioLogging = "dev.zio" %% "zio-logging" % zioLoggingVersion

  val tapir = List(
    "com.softwaremill.sttp.tapir" %% "tapir-zio"                % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui"         % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc"              % tapirVersion
  )

  val jintellitype = "com.melloware" % "jintellitype" % jintellitypeVersion

  val thumbnailator = "net.coobird"  % "thumbnailator"   % "0.4.14"
  val javacv        = "org.bytedeco" % "javacv-platform" % "1.5.6"

  val kindProjector = "org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full

  val quill = List("io.getquill" %% "quill-codegen-jdbc" % quillVersion, "io.getquill" %% "quill-async-mysql" % quillVersion)
  def scalaReflect(scalaVersion: String) = "org.scala-lang" % "scala-reflect" % scalaVersion

  lazy val javaFXModules = List("base", "controls", "fxml", "graphics", "media", "swing", "web")

  val scalafx = {
    // Add dependency on ScalaFX library
    val fx = "org.scalafx" %% "scalafx" % "16.0.0-R25"

    // Determine OS version of JavaFX binaries
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux")   => "linux"
      case n if n.startsWith("Mac")     => "mac"
      case n if n.startsWith("Windows") => "win"
      case _                            => throw new Exception("Unknown platform!")
    }

    // Add dependency on JavaFX libraries, OS dependent
    val javafx = javaFXModules.map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
    fx :: javafx
  }

  val distage = "io.7mind.izumi" %% "distage-core" % distageVersion

  val zio = "dev.zio" %% "zio" % zioVersion

}
