package org.xarcher.sbt

import sbt._
import sbt.Keys._

object Dependencies {

  //repl
  val ammoniteRepl = Seq(
    //"com.lihaoyi" % "ammonite" % "0.8.2" % "test" cross CrossVersion.full
  )

  val logback = Seq(
    "ch.qos.logback" % "logback-core",
    "ch.qos.logback" % "logback-classic"
  ) map (_ % "1.2.3")

  val slickVersion = "3.2.0"
  val slickPgVersion = "0.15.0-RC"
  val slick = Seq(
    //"net.sf.ucanaccess" % "ucanaccess" % "4.0.1",
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-codegen" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion exclude("com.zaxxer", "HikariCP-java6"),
    "com.typesafe.play" %% "play-slick" % "3.0.0-M3" exclude("com.zaxxer", "HikariCP-java6") exclude ("com.typesafe.slick", "slick")
    //"mysql" % "mysql-connector-java" % "5.1.41"
    //"org.apache.commons" % "commons-lang3" % "3.3.2",
    //"org.joda" % "joda-convert" % "1.7",
    //"com.vividsolutions" % "jts" % "1.13",
    //"com.github.tminglei" %% "slick-pg" % slickPgVersion,
    //"com.github.tminglei" %% "slick-pg_jts" % slickPgVersion,
    //"com.github.tminglei" %% "slick-pg_joda-time" % slickPgVersion,
    //"com.github.tminglei" %% "slick-pg_circe-json" % slickPgVersion exclude ("io.circe", "circe-parser_2.11") exclude ("io.circe", "circe-core_2.11") exclude ("io.circe", "circe-generic_2.11")
  )

  val webjars = Seq(
    //webjars
    "org.webjars" % "json2" % "20140204",
    "org.webjars" % "knockout" % "3.4.0",
    "org.webjars.bower" % "knockout-mapping" % "2.4.1",
    "org.webjars" % "jquery" % "1.12.0",
    "org.webjars" % "requirejs" % "2.1.22",
    "org.webjars" % "requirejs-text" % "2.0.14-1",
    "org.webjars.bower" % "echarts" % "3.2.2"
  )

  val play2AuthVersion = "0.14.2"
  val play2Auth = Seq(
    //security
    //"jp.t2v" %% "play2-auth" % play2AuthVersion,
    //"jp.t2v" %% "play2-auth-social" % play2AuthVersion,
    //"jp.t2v" %% "play2-auth-test" % play2AuthVersion % "test",
    play.sbt.Play.autoImport.cache
  )

  val circeVersion = "0.7.0"

  val circeDependenciesForPlayCaster = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion//,
    //"play-circe" %% "play-circe" % "2.5-0.7.0"
  )

  val scalaAsync = Seq(
    "org.scala-lang.modules" %% "scala-async" % "0.9.6-RC2"
  )

  val jgitVersion = "4.4.1.201607150455-r"
  val jfxGit =
    ("org.scalafx" %% "scalafx" % "8.0.102-R11") ::
    (
      List(
        "org.eclipse.jgit" % "org.eclipse.jgit",
        "org.eclipse.jgit" % "org.eclipse.jgit.pgm",
        "org.eclipse.jgit" % "org.eclipse.jgit.http.server",
        "org.eclipse.jgit" % "org.eclipse.jgit.ui",
        "org.eclipse.jgit" % "org.eclipse.jgit.junit"
      ) map (
        _ % jgitVersion
        exclude("javax.jms", "jms")
        exclude("com.sun.jdmk", "jmxtools")
        exclude("com.sun.jmx", "jmxri")
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("commons-logging", "commons-logging")
      )
    )

  val commonsNet = Seq(
    "commons-net" % "commons-net" % "3.5"
  )

  val quartz = Seq(
    "org.quartz-scheduler" % "quartz",
    "org.quartz-scheduler" % "quartz-jobs"
  ).map(_ % "2.2.3")

  val jsEngine = Seq(
    "org.jsoup" % "jsoup" % "1.10.2",
    /*(if (scala.util.Properties.isWin) {
      "com.eclipsesource.j2v8" % "j2v8_win32_x86_64" % "4.6.0"
    } else if (scala.util.Properties.isMac) {
      "com.eclipsesource.j2v8" % "j2v8_macosx_x86_64" % "4.6.0"
    } else {
      "com.eclipsesource.j2v8" % "j2v8_linux_x86_64" % "4.7.0"
    })*/
    "com.github.automately" % "j2v8_all_x86_64" % "4.7.0"
  )
  //val parboiled = Seq("org.parboiled" %% "parboiled" % "2.1.4")
}