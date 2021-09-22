package desu.video.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter

import com.typesafe.config.ConfigFactory

import desu.video.akka.model.FileNotConfirmException

import java.nio.file.{Files, Path, Paths}

import scala.concurrent.Future

object AppConfig {
  val defaultDispatcherName = "desu-dispatcher"
}
class AppConfig(system: ActorSystem[Nothing]) {
  implicit val executionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())

  val dirPath = ConfigFactory.load().getString("desu.video.file.rootPath")

  def rootPath(implicit logger: LoggingAdapter): Future[Path] = {
    val path       = Paths.get(dirPath)
    def isConfirmF = Future(!Files.exists(path) || !Files.isDirectory(path))(blockExecutionContext)
    def result(isConfirm: Boolean): Future[Path] = if (isConfirm) {
      val message = s"App root file not exists or app root file is not a directory. Root file path is $dirPath"
      logger.error(message)
      Future.failed(FileNotConfirmException("App root file not exists or app root file is not a directory."))
    } else Future(path)

    for {
      isConfirm <- isConfirmF
      f         <- result(isConfirm)
    } yield f
  }

}
