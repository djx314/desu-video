package desu.video.akka.routes.test

import com.softwaremill.macwire._
import desu.video.akka.config.AppConfig
import desu.video.common.slick.DesuDatabase

import scala.concurrent.ExecutionContext

object TestWire {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(null)
  lazy val appConfig                        = wire[AppConfig]
  lazy val desuDatabase                     = wire[DesuDatabase]

}
