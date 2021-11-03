package org.scalax.sbt

import sbt._
import sbt.Keys._

object CustomSettings {

  val scalaConfig = Seq(scalaVersion := "2.13.7", scalacOptions ++= Seq("-feature", "-deprecation", "-Ymacro-annotations"))

  val fmtConfig = org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

}
