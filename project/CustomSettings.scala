package org.scalax.sbt

import sbt._
import sbt.Keys._

object CustomSettings {

  val scalaVersion_213 = "2.13.8"
  val scalaVersion_3   = "3.1.2"

  val scalaConfig  = Seq(scalaVersion := scalaVersion_213, scalacOptions ++= Seq("-feature", "-deprecation", "-Ymacro-annotations"))
  val scala3Config = Seq(scalaVersion := scalaVersion_3, scalacOptions ++= Seq("-feature", "-deprecation", "-rewrite"))

  val fmtConfig = org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

  val crossConfig = crossScalaVersions := Seq(scalaVersion_213, scalaVersion_3)

}
