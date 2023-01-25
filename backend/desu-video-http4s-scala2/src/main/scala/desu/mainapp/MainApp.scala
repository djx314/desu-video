package desu.mainapp

import com.comcast.ip4s._
import cats.effect._
import org.http4s.ember.server.EmberServerBuilder

object MainApp extends IOApp {

  val serverBuilding = EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080")

  val serverResource = for {
    routesApp <- MainAppInjected.appRoutes
    server    <- serverBuilding.withHttpApp(routesApp.orNotFound).build
  } yield server

  override def run(args: List[String]): IO[ExitCode] = serverResource.useForever.as(ExitCode.Success)

}
