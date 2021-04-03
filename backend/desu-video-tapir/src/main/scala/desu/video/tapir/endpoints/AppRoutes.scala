package desu.video.tapir.endpoints

import cats.effect.{Blocker, ContextShift}
import cats.implicits._

import desu.video.tapir.handlings.VideoHanding
import desu.video.tapir.mainapp.Layers

import java.util.concurrent._

import org.http4s.HttpRoutes
import org.http4s.server.staticcontent._

import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.apispec.Tag
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.docs.openapi._

import zio.interop.catz._

object AppRoutes {

  val docs: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(VideoEndpoints.docs, "desu-video", "1.0").tags(List(Tag("前台页面响应", Option("前台页面响应"))))

  val blockingPool = Executors.newFixedThreadPool(4)
  val blocker      = Blocker.liftExecutorService(blockingPool)

  val http4sSwaggerRoutes = new SwaggerHttp4s(docs.toYaml)
  def routes(implicit cs: ContextShift[Layers.ZIOEnvTask]): HttpRoutes[Layers.ZIOEnvTask] = {
    val fileRoutes = fileService[Layers.ZIOEnvTask](FileService.Config("D:/xlxz", blocker, pathPrefix = "eeff"))
    ZHttp4sServerInterpreter.from(VideoHanding.routes).toRoutes <+> http4sSwaggerRoutes.routes <+> fileRoutes
  }

}