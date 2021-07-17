package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val AkkaVersion           = "2.6.15"
  val AkkaHttpVersion       = "10.2.4"
  val akkaHttpCirceVersion  = "1.37.0"
  val slf4jVersion          = "1.7.31"
  val typeSafeConfigVersion = "1.4.1"

  val config = List("com.typesafe" % "config" % typeSafeConfigVersion)

  val simpleLogger = List("org.slf4j" % "slf4j-simple" % slf4jVersion)

  val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream"      % AkkaVersion,
    "com.typesafe.akka" %% "akka-http"        % AkkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-circe"  % akkaHttpCirceVersion
  )

  val http4sVersion = "1.0.0-M23"

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion
  )

  val catsVersion = "3.1.1"

  val cats = Seq("org.typelevel" %% "cats-effect" % "3.0.0")

  val circeVersion = "0.14.1"
  val circe = Seq(
    "io.circe" %% "circe-core"    % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser"  % circeVersion
  )

  val macwireVersion = "2.3.7"

  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros"     % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "macrosakka" % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "util"       % macwireVersion,
    "com.softwaremill.macwire" %% "proxy"      % macwireVersion
  )

}
