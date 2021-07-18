package desu.video.akka.mainapp

import scala.concurrent.ExecutionContext
import com.softwaremill.macwire._
import desu.video.akka.config.AppConfig
import desu.video.akka.service.FileService
import desu.video.common.slick.DesuDatabase

object MainApp {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(null)
  private lazy val appConfig                = wire[AppConfig]
  private lazy val desuDatabase             = wire[DesuDatabase]

  lazy val fileList = wire[FileService]

}
