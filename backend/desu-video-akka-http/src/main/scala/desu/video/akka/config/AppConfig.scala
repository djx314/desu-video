package desu.video.akka.config

import akka.event.LoggingAdapter
import com.typesafe.config.ConfigFactory
import desu.video.akka.model.FileNotConfirmException

import java.nio.file.{Files, Path, Paths}

class AppConfig {

  val dirPath = ConfigFactory.load().getString("desu.video.file.rootPath")

  def rootPath(implicit logger: LoggingAdapter): Either[FileNotConfirmException, Path] = {
    val path = Paths.get(dirPath)
    if (!Files.exists(path) || !Files.isDirectory(path)) {
      val message = s"App root file not exists or app root file is not a directory. Root file path is $dirPath"
      logger.error(message)
      Left(FileNotConfirmException("App root file not exists or app root file is not a directory."))
    } else Right(path)
  }

}
