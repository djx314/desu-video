package desu.routes

import com.softwaremill.macwire._
import desu.config.AppConfig
import org.http4s.server.staticcontent._
import org.http4s._
import org.http4s.dsl.io._
import desu.service.RootFilePathsService
import cats.implicits._
import zio._
import zio.interop.catz._

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

object AppRoutes {

  // type ZIORuntimeEnv[T] = RIO[ZEnv with clock.Clock with blocking.Blocking, T]
  type ZIOEnv[T] = RIO[ZEnv, T]

  private lazy val filePageRoute: FilePageRoute               = wire[FilePageRoute]
  private lazy val rootFilePathsService: RootFilePathsService = wire[RootFilePathsService]

  private lazy val appConfig: AppConfig = wire[AppConfig]

  /*val routes1: HttpRoutes[IO] = {
    filePageRoute.rootPathFiles <+> filePageRoute.rootDirName <+> filePageRoute.baiduPage <+> fileRoutes
  }*/

  private val fileRoutes = fileService[ZIOEnv](FileService.Config("D:/xlxz", pathPrefix = "eeff"))
  private val httpRoutes = ZHttp4sServerInterpreter[ZEnv]().from(filePageRoute.routes).toRoutes

  private val docsAsYaml: String = OpenAPIDocsInterpreter().toOpenAPI(filePageRoute.docs, "Desu App", "1.0").toYaml
  private val swaggerUIRoute     = ZHttp4sServerInterpreter[ZEnv]().from(SwaggerUI[ZIOEnv](docsAsYaml)).toRoutes

  val routes: HttpRoutes[ZIOEnv] = httpRoutes <+> fileRoutes <+> swaggerUIRoute

}
