package org.scalax.sbt

import sbt._
import sbt.Keys._

object CustomSettings {

  val scalaConfig  = Seq(scalaVersion := "2.13.8", scalacOptions ++= Seq("-feature", "-deprecation", "-Ymacro-annotations"))
  val scala3Config = Seq(scalaVersion := "3.1.2", scalacOptions ++= Seq("-feature", "-deprecation"))

  val fmtConfig = org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

}
