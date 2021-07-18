package desu.video.akka.config

import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}

class AppConfig {

  val dirPath = ConfigFactory.load().getString("desu.video.file.rootPath")

  private def getRootPath = {
    val path = Paths.get(dirPath)
    if (!Files.exists(path) || !Files.isDirectory(path)) {
      throw new IllegalArgumentException("App root file not exists or app root file is not a directory.")
    }
    path
  }

  val rootPath = getRootPath

}
