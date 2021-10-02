package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter
import desu.video.akka.config.AppConfig
import desu.video.akka.model.{FileNotConfirmException, RootPathFiles}

import java.nio.file.{Files, Path}
import java.util.stream.Collectors
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

class FileFinder(appConfig: AppConfig)(implicit system: ActorSystem[Nothing]) {
  implicit val executionContext = system.dispatchers.lookup(appConfig.desuSelector)
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())

  /** @throws FileNotConfirmException
    * @return
    */
  def rootPathFiles(implicit logger: LoggingAdapter): Future[RootPathFiles] = {
    def fileList(path: Path) = Files.list(path).map(_.toFile.getName).collect(Collectors.toList[String])

    for {
      path  <- appConfig.rootPath
      model <- Future(fileList(path))(blockExecutionContext)
    } yield {
      val files = model.asScala.to(List)
      RootPathFiles(files = files)
    }
  }

}
