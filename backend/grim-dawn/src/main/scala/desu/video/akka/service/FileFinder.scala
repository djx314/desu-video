package desu.video.akka.service

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import akka.event.LoggingAdapter

import desu.video.akka.config.AppConfig

import java.awt.image.BufferedImage
import java.nio.file.{Path, Paths}
import java.awt.{Rectangle, Robot, Toolkit}
import java.util.Date

import javax.imageio.ImageIO

import scala.util.{Failure, Success}
import scala.concurrent.Future

class FileFinder(appConfig: AppConfig)(implicit system: ActorSystem[Nothing]) {
  implicit val executionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())

  def getDesktopFile(implicit logger: LoggingAdapter): Future[Path] = {
    val defaultImageFormat = "png"

    val screenshotF = Future {
      val d = Toolkit.getDefaultToolkit.getScreenSize
      new Robot().createScreenCapture(new Rectangle(0, 0, d.width, d.height))
    }(blockExecutionContext)

    val fileNameF = Future(new Date().getTime.toString + s".$defaultImageFormat")
    val pathF     = for (fileName <- fileNameF) yield Paths.get(".", "backend", "grim-dawn", "target", fileName)
    def writeIO(buffer: BufferedImage, path: Path): Future[Boolean] =
      Future(ImageIO.write(buffer, defaultImageFormat, path.toFile))(blockExecutionContext)

    val action = for {
      screenshot <- screenshotF
      path       <- pathF
      _          <- writeIO(screenshot, path)
    } yield path

    action.onComplete {
      case Success(value)     =>
      case Failure(exception) => logger.error(exception, "获取屏幕截图出现错误")
    }

    action
  }

}
