package desu.video.akka.mainapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext
import com.softwaremill.macwire._
import desu.video.akka.config.AppConfig
import desu.video.akka.service.FileService
import desu.video.common.slick.DesuDatabase

object MainApp {

  implicit val system = ActorSystem(Behaviors.empty, "my-system")

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(null)
  private lazy val appConfig                = wire[AppConfig]
  private lazy val desuDatabase             = wire[DesuDatabase]

  lazy val fileService = wire[FileService]

}
