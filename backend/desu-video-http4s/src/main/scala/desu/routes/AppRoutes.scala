package desu.routes

import cats.effect._
import cats.implicits._
import com.softwaremill.macwire._

import desu.config.AppConfig

import org.http4s.server.staticcontent._
import org.http4s._
import org.http4s.dsl.io._

object AppRoutes {

  private val fileRoutes = fileService[IO](FileService.Config("D:/xlxz", pathPrefix = "eeff"))

  private lazy val filePageRoute: FilePageRoute = wire[FilePageRoute]

  private lazy val appConfig: AppConfig = wire[AppConfig]

  val routes: HttpRoutes[IO] = {
    filePageRoute.firstRoute <+> fileRoutes
  }

}
