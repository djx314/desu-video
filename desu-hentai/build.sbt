import sbt._
import sbt.Keys._

lazy val playVersion = play.core.PlayVersion.current

transitiveClassifiers in ThisBuild := Seq("sources", "jar", "javadoc")

scalacOptions ++= Seq("-encoding", "UTF-8", "-feature", "-deprecation")

name := "desu-hentai"
version := "0.0.1"

val circeVersion       = "0.12.2"
val commonCirceVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-literal"
).map(_ % commonCirceVersion)

libraryDependencies += "org.scalax" %% "kirito" % "0.0.1-20200325SNAP1"

libraryDependencies += ws
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.2"
libraryDependencies += "commons-io"        % "commons-io"              % "2.6"
libraryDependencies += "com.dripower"      %% "play-circe"             % "2812.0"
libraryDependencies += "io.circe"          %% "circe-generic-extras"   % circeVersion
libraryDependencies += "org.scalax"        %% "asuna-macros"           % "0.0.3-20200325SNAP1"
//libraryDependencies += "org.apache.httpcomponents" % "httpmime" % "4.5.3"

libraryDependencies ++= Seq(
  "com.softwaremill.macwire" %% "macros"     % "2.3.3" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.3" % "provided",
  "com.softwaremill.macwire" %% "util"       % "2.3.3",
  "com.softwaremill.macwire" %% "proxy"      % "2.3.3"
)

val http4sVersion = "0.21.2"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies ++= Seq(
  "org.webjars.bower" % "requirejs"      % "2.3.6",
  "org.webjars.bower" % "requirejs-text" % "2.0.15"
)

scalaVersion := "2.13.1"
fork in run := false
enablePlugins(play.sbt.PlayScala, PlayAkkaHttpServer)
disablePlugins(PlayNettyServer)
addCommandAlias("drun", "run 3141")

dependsOn(hentaiBase)
lazy val hentaiBase = (project in file("./hentai-base"))

org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true
