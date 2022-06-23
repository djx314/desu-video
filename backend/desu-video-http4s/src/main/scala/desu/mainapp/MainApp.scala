package desu.mainapp

import desu.routes.AppRoutes

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.given
import scala.concurrent.duration.given

object MainApp extends IOApp:

  val mainAppInjected = new MainAppInjected

  val serverResource =
    for
      routesApp <- mainAppInjected.appRoutes
      server    <- EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(routesApp.routes).build
    yield server

  override def run(args: List[String]): IO[ExitCode] = serverResource.use(_ => IO.never).as(ExitCode.Success)

end MainApp
