package desu.routes

import desu.config.AppConfig
import desu.endpoint.DesuEndpoint
import desu.models.ResultSet
import io.circe.syntax._
import sttp.client3._
import desu.service.RootFilePathsService
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.model.{Uri => SUri}
import sttp.tapir._
import sttp.tapir.ztapir._
import zio._

class FilePageRoute(appConfig: AppConfig, rootFilePathsService: RootFilePathsService) {

  val FilePageRoot = appConfig.FilePageRoot

  val baiduPage = DesuEndpoint.baiduPageEndpoint.zServerLogic { (_: Unit) =>
    val backendResource = AsyncHttpClientZioBackend.managed()
    def requestUri(uri: SUri) = backendResource.use(backend =>
      for {
        request  <- ZIO.effect(basicRequest.get(uri))
        response <- request.send(backend)
        l4       <- ZIO.succeed(response.body.merge)
      } yield l4
    )
    requestUri(uri"http://www.baidu.com").mapError(e => "请求错误")
  }

  /*val rootPathFiles = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFile" / dirName =>
    for {
      dirInfo <- rootFilePathsService.rootPathDirInfo(dirName)
      result  <- Ok(ResultSet(dirInfo).asJson)
    } yield result
  }

  val rootDirName = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFiles" =>
    for {
      dirInfo <- rootFilePathsService.rootPathDirName
      result  <- Ok(ResultSet(dirInfo).asJson)
    } yield result
  }*/

  val routes = List(baiduPage.widen[ZEnv])
  val docs   = List(DesuEndpoint.baiduPageEndpoint)

}
