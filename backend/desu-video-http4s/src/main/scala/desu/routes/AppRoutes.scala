package desu.routes

import cats.implicits._

import desu.mainapp.common._

import org.http4s.server.staticcontent._
import org.http4s.HttpRoutes

import sttp.tapir.ztapir._
import sttp.tapir.server.http4s.ztapir._

import zio.interop.catz._

object AppRoutes {
  import sttp.tapir.openapi.circe.yaml._
  import sttp.tapir.swagger.http4s.SwaggerHttp4s

  val sumRoutes: List[ZServerEndpoint[AppContext, _, _, _]] = List.empty
  val tapirRoutes                                           = ZHttp4sServerInterpreter[AppContext]().from(sumRoutes).toRoutes

  // val http4sSwaggerRoutes = new SwaggerHttp4s(MainEndpoint.docs.toYaml)
  def routes: HttpRoutes[ZIOEnvTask] = {
    val fileRoutes = fileService[ZIOEnvTask](FileService.Config("D:/xlxz", pathPrefix = "eeff"))
    tapirRoutes <+> /*http4sSwaggerRoutes.routes <+>*/ fileRoutes
  }

}