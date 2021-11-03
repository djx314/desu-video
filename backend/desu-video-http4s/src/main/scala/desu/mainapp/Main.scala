package desu.mainapp

import cats.effect._
import desu.routes.AppRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.duration._

object Main extends IOApp {

  val builder = BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(AppRoutes.routes.orNotFound)
    .withIdleTimeout(10.minutes)
    .withResponseHeaderTimeout(10.minutes)

  override def run(args: List[String]): IO[ExitCode] = {
    builder.serve.compile.drain.as(ExitCode.Success)
  }

}
