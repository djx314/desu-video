package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val AkkaVersion           = "2.6.15"
  val AkkaHttpVersion       = "10.2.4"
  val akkaHttpCirceVersion  = "1.37.0"
  val slf4jVersion          = "1.7.31"
  val typeSafeConfigVersion = "1.4.1"
  val scalatestVersion      = "3.2.9"
  val macwireVersion        = "2.3.7"
  val circeVersion          = "0.14.1"
  val catsVersion           = "3.1.1"
  val http4sVersion         = "0.23.1"
  val slickVersion          = "3.3.3"
  val mysqlVersion          = "8.0.25"
  val tapirVersion          = "0.19.0-M4"

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
    "org.http4s" %% "http4s-blaze-client" % http4sVersion
  )

  val cats = Seq("org.typelevel" %% "cats-effect" % "3.2.2")

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
