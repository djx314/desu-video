package org.scalax.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  object versions {
    val akka           = "2.6.17"
    val akkaHttp       = "10.2.7"
    val akkaHttpCirce  = "1.38.2"
    val slf4j          = "1.7.32"
    val typeSafeConfig = "1.4.1"
    val http4s         = "0.23.7"
    val catsEffect     = "3.3.11"
    val catsEffect2    = "2.5.4"
    val circe          = "0.15.0-M1"
    val kindProjector  = "0.13.2"
    val scalatest      = "3.2.12"
    val macwire        = "2.5.3"
    val mysql          = "8.0.29"
    val slick          = "3.4.0-M1"
    val sttp           = "3.3.18"
    val tapir          = "1.0.0-M9"
    val zioLogging     = "0.5.14"
    val jintellitype   = "1.4.0"
    val quill_old      = "3.17.0-RC4"
    val quill          = "3.17.0.Beta3.0-RC4"
    val distage        = "1.0.8"
    val zio            = "1.0.9"
    val zio2           = "2.0.0-RC5"
    val zioMagic       = "0.3.11"
    val zioHttp        = "2.0.0-RC7"
    val finch = "0.33.0"
    val scalaCollectionCompat = "2.7.0"
    val scalaJava8Compat = "1.0.2"
  }

  val config = List("com.typesafe" % "config" % versions.typeSafeConfig)

  val simpleLogger = List("org.slf4j" % "slf4j-simple" % versions.slf4j)

  val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-actor-typed"    % versions.akka,
    "com.typesafe.akka" %% "akka-stream"         % versions.akka,
    "com.typesafe.akka" %% "akka-http"           % versions.akkaHttp,
    "de.heikoseeberger" %% "akka-http-circe"     % versions.akkaHttpCirce,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka,
    "com.typesafe.akka" %% "akka-http-testkit"   % versions.akkaHttp
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % versions.http4s,
    "org.http4s" %% "http4s-blaze-server" % versions.http4s,
    "org.http4s" %% "http4s-blaze-client" % versions.http4s,
    "org.http4s" %% "http4s-circe"        % versions.http4s
  )

  val cats  = Seq("org.typelevel" %% "cats-effect" % versions.catsEffect)
  val cats2 = Seq("org.typelevel" %% "cats-effect" % versions.catsEffect2)

  val circe = Seq(
    "io.circe" %% "circe-core"    % versions.circe,
    "io.circe" %% "circe-generic" % versions.circe,
    "io.circe" %% "circe-parser"  % versions.circe
  )

  val macwire = Seq(
    "com.softwaremill.macwire" %% "macros"     % versions.macwire % "provided",
    "com.softwaremill.macwire" %% "macrosakka" % versions.macwire % "provided",
    "com.softwaremill.macwire" %% "util"       % versions.macwire,
    "com.softwaremill.macwire" %% "proxy"      % versions.macwire
  )

  val scalatest = Seq(
    "org.scalactic" %% "scalactic" % versions.scalatest,
    "org.scalatest" %% "scalatest" % versions.scalatest % "test"
  )

  val mysql = Seq("mysql" % "mysql-connector-java" % versions.mysql)

  val slick = Seq(
    "com.typesafe.slick" %% "slick"          % versions.slick,
    "com.typesafe.slick" %% "slick-codegen"  % versions.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % versions.slick
  )

  val sttp = List(
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio"  % versions.sttp,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % versions.sttp
  )

  val zioLogging = "dev.zio" %% "zio-logging" % versions.zioLogging

  val tapir = List(
    "com.softwaremill.sttp.tapir" %% "tapir-zio"                % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui"         % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-redoc"              % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"    % versions.tapir
  )

  val jintellitype = "com.melloware" % "jintellitype" % versions.jintellitype

  val thumbnailator = "net.coobird"  % "thumbnailator"   % "0.4.14"
  val javacv        = "org.bytedeco" % "javacv-platform" % "1.5.6"

  val kindProjector = "org.typelevel" % "kind-projector" % versions.kindProjector cross CrossVersion.full

  val quill = List("io.getquill" %% "quill-codegen-jdbc" % versions.quill_old, "io.getquill" %% "quill-jasync-mysql" % versions.quill_old,"io.github.kitlangton" %% "zio-magic"      % versions.zioMagic)
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

  val distage = "io.7mind.izumi" %% "distage-core" % versions.distage

  val zio  = "dev.zio" %% "zio" % versions.zio
  val zio2 = "dev.zio" %% "zio" % versions.zio2

  val zioHttp = Seq("io.d11" %% "zhttp" % versions.zioHttp, "io.d11" %% "zhttp-test" % versions.zioHttp % Test)

  val quill_scala3 = Seq(
    "io.getquill" %% "quill-jdbc-zio" % versions.quill
    // "io.github.kitlangton" %% "zio-magic"      % versions.zioMagic
  )

  val finch = Seq(
    "com.github.finagle" %% "finchx-core" % versions.finch,
    "com.github.finagle" %% "finchx-generic" % versions.finch
  )

  val scalaCollectionCompat = List("org.scala-lang.modules" %% "scala-collection-compat" % versions.scalaCollectionCompat)
  val scalaJava8Compat = List("org.scala-lang.modules" %% "scala-java8-compat" %   versions.scalaJava8Compat)

}
