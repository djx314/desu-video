package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val tapirVersion = "0.18.0-M1"
  val zio = List(
    "dev.zio" %% "zio-interop-cats" % "2.3.1.0",
    "dev.zio" %% "zio"              % "1.0.5",
    "dev.zio" %% "zio-logging"      % "0.5.8"
  )

  val sttp = List("com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.2.0")

  val tapirCore = List("com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion)
  val tapir = List(
    "com.softwaremill.sttp.tapir" %% "tapir-zio"                % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirVersion,
    // HTTP4S
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"      % tapirVersion
  )

  val config = List("com.typesafe" % "config" % "1.4.1")

  val simpleLogger = List("org.slf4j" % "slf4j-simple" % "1.7.30")

}
