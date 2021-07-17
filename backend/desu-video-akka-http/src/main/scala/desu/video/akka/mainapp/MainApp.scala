package desu.video.akka.mainapp

import desu.video.akka.routes.FileService

import scala.concurrent.ExecutionContext

import com.softwaremill.macwire._

object MainApp {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(null)

  val fileList = wire[FileService]

}
