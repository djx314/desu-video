package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val xioVersion   = "0.0.1-SNAPSHOT"
  val tapirVersion = "0.17.19"
  val zio = List(
    "dev.zio" %% "zio-interop-cats" % "2.3.1.0",
    "dev.zio" %% "zio"              % "1.0.5",
    "dev.zio" %% "zio-logging"      % "0.5.8"
  )

  val sttp = List("com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.2.0")

  val tapir = List(
    "com.softwaremill.sttp.tapir" %% "tapir-zio"                % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % tapirVersion,
    // HTTP4S
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"      % tapirVersion
  )

}
