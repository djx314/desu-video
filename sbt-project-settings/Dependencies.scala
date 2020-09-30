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
    "io.circe" %% "circe-generic-extras" % circeVersion,
    "com.dripower" %% "play-circe" % "2812.0"
  )

  val ffmpeg = "net.bramp.ffmpeg" % "ffmpeg" % "0.6.2"

  val playVersion = play.core.PlayVersion.current
  val playLib = "com.typesafe.play" %% "play" % playVersion % "provided"

}
