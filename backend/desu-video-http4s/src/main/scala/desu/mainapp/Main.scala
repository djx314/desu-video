package desu.mainapp

import desu.routes.AppRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import zio._
import zio.interop.catz._

import scala.concurrent.duration._

object Main1 extends App {

  val builder = BlazeServerBuilder[AppRoutes.ZIOEnv]
    .bindHttp(8080, "localhost")
    .withHttpApp(AppRoutes.routes.orNotFound)
    .withIdleTimeout(10.minutes)
    .withResponseHeaderTimeout(10.minutes)

  override def run(args: List[String]): URIO[ZEnv, zio.ExitCode] = builder.serve.compile.drain.exitCode

}
