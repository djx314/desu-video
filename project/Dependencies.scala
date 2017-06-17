package org.xarcher.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  val scalaAsync = Seq(
    "org.scala-lang.modules" %% "scala-async" % "0.9.6-RC2"
  )

  val commonsNet = Seq(
    "commons-net" % "commons-net" % "3.5"
  )

  val quartz = Seq(
    "org.quartz-scheduler" % "quartz",
    "org.quartz-scheduler" % "quartz-jobs"
  ).map(_ % "2.2.3")

}