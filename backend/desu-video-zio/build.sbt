import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scalaConfig
org.scalax.sbt.CustomSettings.fmtConfig

name := "desu-video-zio"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= Dependencies.cats
libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.slick
libraryDependencies ++= Dependencies.scalaCollectionCompat
libraryDependencies += "org.julienrf" %% "play-json-derived-codecs" % "10.0.2"
libraryDependencies +="com.softwaremill.sttp.tapir"   %% "tapir-jsoniter-scala"        % Dependencies.versions.tapir
libraryDependencies ++= Dependencies.jsoniter