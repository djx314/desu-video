package desu.mainapp

import desu.routes.AppRoutes

import com.comcast.ip4s._
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import scala.concurrent.duration._

object MainApp extends IOApp {

  val serverBuilding = EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080")

  val serverResource = for {
    routesApp <- MainAppInjected.appRoutes
    server    <- serverBuilding.withHttpApp(routesApp.routes).build
  } yield server

  override def run(args: List[String]): IO[ExitCode] = serverResource.useForever.as(ExitCode.Success)

}
