package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import com.softwaremill.macwire._
import desu.video.akka.config.AppConfig
import desu.video.akka.routes.HttpServerRoutingMinimal
import desu.video.akka.service.{FileFinder, FileService}
import desu.video.common.slick.DesuDatabase

import scala.concurrent.ExecutionContext

case class TestWire()(implicit val system: ActorSystem[Nothing]) {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(null)
  lazy val appConfig                        = wire[AppConfig]
  lazy val desuDatabase                     = wire[DesuDatabase]

  private lazy val fileService = wire[FileService]
  private lazy val fileFinder  = wire[FileFinder]

  lazy val routingMinimal = wire[HttpServerRoutingMinimal]

}
