libraryDependencies += {
  val playVersion = play.core.PlayVersion.current
  "com.typesafe.play" %% "play" % playVersion % "provided"
}

libraryDependencies += "net.bramp.ffmpeg" % "ffmpeg" % "0.6.1"