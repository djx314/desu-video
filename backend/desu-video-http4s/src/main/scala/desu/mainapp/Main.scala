package desu.mainapp

import desu.mainapp.common._
import desu.routes.AppRoutes

import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.duration._

import zio._
import zio.interop.catz._

object Main extends zio.App {

  private def startApp(runtime: Runtime[AppEnv]): ZIOEnvTask[Unit] = {
    val builder = BlazeServerBuilder[ZIOEnvTask](runtime.platform.executor.asEC)
    val builder2 = builder
      .bindHttp(8080, "192.168.1.105")
      .withHttpApp(AppRoutes.routes.orNotFound)
      .withIdleTimeout(10.minutes)
      .withResponseHeaderTimeout(10.minutes)
    builder2.serve.compile.drain
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val action1 = ZIO.runtime[AppEnv].flatMap(startApp)
    action1.provideCustomLayer(applicationContenxtlive).exitCode
  }

}
