import org.scalax.sbt.Dependencies

org.scalax.sbt.CustomSettings.scala3Config
org.scalax.sbt.CustomSettings.fmtConfig

name       := "desu-video-test"
moduleName := "desu-video-test"

libraryDependencies ++= Dependencies.scalatest
libraryDependencies ++= libScalax.`slf4j-simple`.value
libraryDependencies += Dependencies.hikariCP
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.macwire
libraryDependencies ++= Dependencies.sttp map (_ exclude ("org.scala-lang.modules", "scala-collection-compat_2.13"))

libraryDependencies ++= Dependencies.zio2
libraryDependencies ++= Dependencies.tapir
libraryDependencies ++= Dependencies.zioHttp
libraryDependencies ++= Dependencies.quill_scala3
libraryDependencies ++= libScalax.`scala-collection-compat`.value
libraryDependencies +="com.softwaremill.sttp.tapir"   %% "tapir-jsoniter-scala"        % Dependencies.versions.tapir
libraryDependencies ++= Dependencies.jsoniter

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")


