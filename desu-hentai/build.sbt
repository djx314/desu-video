import sbt._
import sbt.Keys._
import org.scalax.sbt.CustomSettings
import org.scalax.sbt.Dependencies

lazy val playVersion = play.core.PlayVersion.current

transitiveClassifiers in ThisBuild := Seq("sources", "jar", "javadoc")

scalacOptions ++= Seq("-encoding", "UTF-8", "-feature", "-deprecation")

name := "desu-hentai"
version := "0.0.1"

libraryDependencies += "org.scalax" %% "ugeneric-circe" % "0.0.1-SNAP2020071301"

libraryDependencies += ws
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.2"
libraryDependencies += Dependencies.commonsIO
libraryDependencies ++= Dependencies.circe
libraryDependencies ++= Dependencies.zio
//libraryDependencies += "org.scalax"        %% "asuna-macros"           % "0.0.3-20200325SNAP1"
//libraryDependencies += "org.apache.httpcomponents" % "httpmime" % "4.5.3"

libraryDependencies ++= Dependencies.macwire

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

fork in run := false
enablePlugins(play.sbt.PlayScala, PlayAkkaHttpServer)
disablePlugins(PlayNettyServer)

val hentaiBase = project in file(".") / "hentai-base"
dependsOn(hentaiBase)

CustomSettings.commonProjectSettings

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("drun", "run 3141")
