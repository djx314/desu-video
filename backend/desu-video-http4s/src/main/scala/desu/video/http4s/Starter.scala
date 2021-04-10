package desu.video.http4s

import cats.effect._
import org.http4s._
import cats.syntax.all._

import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.Implicits.global

object Starter extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name.")
  }

  val httpApp = Router("/" -> helloWorldService).orNotFound

  val serverBuilder = BlazeServerBuilder[IO](global).bindHttp(8080, "localhost").withHttpApp(httpApp)

  def run(args: List[String]): IO[ExitCode] = serverBuilder.serve.compile.drain.as(ExitCode.Success)

}
