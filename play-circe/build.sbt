libraryDependencies ++= {
  val playVersion = play.core.PlayVersion.current
  Seq(

    "com.typesafe.play" %% "play" % playVersion % "provided",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe.play" %% "play-test" % playVersion % "test",
    ws
  )
}

libraryDependencies ++= {
  val circeVersion = "0.7.0"
  Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
}