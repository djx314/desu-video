package org.scalax.sbt

import sbt._
import sbt.Keys._

object CustomSettings {

  val scalaConfig = Seq(scalaVersion := "2.13.5", scalacOptions ++= Seq("-feature", "-deprecation"))

  val fmtConfig = org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

}
