package desu.mainapp

import com.comcast.ip4s._
import cats.effect._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.staticcontent._
import cats._
import cats.implicits._
import fs2.io.net.Network
import org.http4s._

object MainApp extends IOApp {

  def serverBuilding[F[_]: Async: Network]: EmberServerBuilder[F] =
    EmberServerBuilder.default[F].withHost(ipv4"0.0.0.0").withPort(port"8080")

  val serverResource = for {
    routesApp <- MainAppInjected.appRoutes
    server    <- serverBuilding[IO].withHttpApp(routesApp.orNotFound).build
  } yield server

  override def run(args: List[String]): IO[ExitCode] = serverResource.useForever.as(ExitCode.Success)

}
