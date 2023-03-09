package org.scalax.sbt

import djx.sbt.depts.output.Djx314DeptsPlugin.autoImport.scalaV
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtSbt
import sbt._
import sbt.Keys._

object CustomSettings {

  val commonConfig = Seq(Compile / compile := ((Compile / compile) dependsOn (Compile / scalafmtSbt)).value)
  val crossConfig  = crossScalaVersions := Seq(scalaV.v213, scalaV.v3)

  val scala213Config: Seq[Setting[_]] = commonConfig ++: Seq(scalacOptions ++= Seq("-feature", "-deprecation", "-Ymacro-annotations"))
  val scala3Config: Seq[Setting[_]]   = commonConfig ++: Seq(scalacOptions ++= Seq("-feature", "-deprecation", "-Ykind-projector"))

}
