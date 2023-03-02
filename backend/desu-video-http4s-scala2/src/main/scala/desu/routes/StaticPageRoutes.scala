package desu.routes

import cats.effect._
import cats.syntax.all._
import desu.config.AppConfig
import desu.mainapp.AAb
import desu.models._
import desu.service.{FileFinder, FileService}
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._

import java.util.UUID

class StaticPageRoutes {

  val `filePath.html` = HttpRoutes.of[IO] { case GET -> Root / "filePath.html" =>
    val parameter = UUID.randomUUID().toString
    Ok(desu.views.html.fileList(parameter))
  }

  private val compatRoutes: HttpRoutes[IO] = `filePath.html`
  val routes: HttpRoutes[IO]               = compatRoutes

}

object StaticPageRoutes {
  def build: HttpRoutes[IO] = {
    val instance = new StaticPageRoutes()
    instance.routes
  }
}
