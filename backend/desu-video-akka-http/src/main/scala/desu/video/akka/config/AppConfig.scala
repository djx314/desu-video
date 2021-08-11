package desu.video.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter

import com.typesafe.config.ConfigFactory

import desu.video.akka.model.FileNotConfirmException

import java.nio.file.{Files, Path, Paths}

import scala.concurrent.{blocking, Future}

class AppConfig(system: ActorSystem[Nothing]) {

  val dirPath = ConfigFactory.load().getString("desu.video.file.rootPath")

  def rootPath(implicit logger: LoggingAdapter): Future[Path] = {
    // AppConfig 极有可能会用到非 blocking 的 ec，故而将 blocking 的 ec 作为函数内部引用。
    implicit val blockExecutionContext = system.dispatchers.lookup(DispatcherSelector.blocking())
    val f = Future {
      val path      = Paths.get(dirPath)
      val isConfirm = blocking(!Files.exists(path) || !Files.isDirectory(path))
      if (isConfirm) {
        val message = s"App root file not exists or app root file is not a directory. Root file path is $dirPath"
        logger.error(message)
        Future.failed(FileNotConfirmException("App root file not exists or app root file is not a directory."))
      } else Future(path)
    }
    f.flatten
  }

}
