import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config
org.scalax.sbt.CustomSettings.fmtConfig

name       := "desu-video-test"
moduleName := "desu-video-test"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= Dependencies.simpleLogger
libraryDependencies ++= Dependencies.circe
libraryDependencies += Dependencies.hikariCP
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.sttp map (_ exclude ("org.scala-lang.modules", "scala-collection-compat_2.13"))

libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.quill_scala3
libraryDependencies ++= Dependencies.scalaCollectionCompat
libraryDependencies +="com.softwaremill.sttp.tapir"   %% "tapir-json-zio"        % Dependencies.versions.tapir

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions ++= Seq("-Ykind-projector")
