package desu.mainapp

import desu.mainapp.common._
import desu.routes.AppRoutes

import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.duration._

import zio._
import zio.interop.catz._

object Main extends zio.App {

  override def run(args: List[String]): URIO[ZEnv, zio.ExitCode] = {
    val action1: RIO[AppEnv, Unit] = ZIO.runtime[AppEnv].flatMap { implicit runtime =>
      val builder = BlazeServerBuilder[ZIOEnvTask](runtime.platform.executor.asEC)
      val builder2 = builder
        .bindHttp(8080, "192.168.1.105")
        .withHttpApp(AppRoutes.routes.orNotFound)
        .withIdleTimeout(10.minutes)
        .withResponseHeaderTimeout(10.minutes)
      builder2.serve.compile.drain
    }
    action1.provideCustomLayer(applicationContenxtlive).exitCode
  }

}