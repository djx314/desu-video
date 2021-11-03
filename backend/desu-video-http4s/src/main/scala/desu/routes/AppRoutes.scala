package desu.routes

import cats.effect._
import cats.implicits._
import com.softwaremill.macwire._
import org.http4s.server.staticcontent._
import org.http4s._
import org.http4s.dsl.io._

object AppRoutes {

  private val fileRoutes = fileService[IO](FileService.Config("D:/xlxz", pathPrefix = "eeff"))

  private lazy val filePageRoute: FilePageRoute = wire[FilePageRoute]

  val routes: HttpRoutes[IO] = {
    filePageRoute.firstRoute <+> fileRoutes
  }

}
