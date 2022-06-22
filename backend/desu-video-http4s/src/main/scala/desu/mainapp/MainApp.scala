package desu.mainapp

import desu.routes.AppRoutes

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.given
import scala.concurrent.duration.given

object MainApp extends IOApp:

  val mainAppInjected = new MainAppInjected

  val server = EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(mainAppInjected.v1.routes).build

  override def run(args: List[String]): IO[ExitCode] = server.use(_ => IO.never).as(ExitCode.Success)

end MainApp
