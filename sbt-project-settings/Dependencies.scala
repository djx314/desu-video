package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val macwireVersion = "2.3.3"

  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "macrosakka" % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "util" % macwireVersion,
    "com.softwaremill.macwire" %% "proxy" % macwireVersion
  )

  val commonsIO = "commons-io" % "commons-io" % "2.6"

  val circeVersion = "0.13.0"

  val circe = List(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,
    "io.circe" %% "circe-generic-extras" % circeVersion,
    "com.dripower" %% "play-circe" % "2812.0"
  )

  val ffmpeg = "net.bramp.ffmpeg" % "ffmpeg" % "0.6.2"

  val playVersion = play.core.PlayVersion.current
  val playLib = "com.typesafe.play" %% "play" % playVersion % "provided"

  val xioVersion = "0.0.1-SNAPSHOT"
  val tapirVersion = "0.17.0-M1"
  val zio = List(
    "dev.zio" %% "zio-interop-cats" % "2.1.4.0",
    "org.scalax.xio" %% "xio" % xioVersion,
    "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.2.8",
    "com.softwaremill.sttp.tapir" %% "tapir-play-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-play" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc-play" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion
  )

}
