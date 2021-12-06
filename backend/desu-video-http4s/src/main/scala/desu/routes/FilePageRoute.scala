package desu.routes

import desu.config.AppConfig
import desu.endpoint.DesuEndpoint
import sttp.client3._
import desu.service.RootFilePathsService
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.model.{StatusCode, Uri => SUri}
import sttp.tapir.ztapir._
import zio._

class FilePageRoute(appConfig: AppConfig, rootFilePathsService: RootFilePathsService) {

  val FilePageRoot = appConfig.FilePageRoot

  val baiduPage = DesuEndpoint.baiduPageEndpoint.zServerLogic { _ =>
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

  val rootPathFile = DesuEndpoint.rootPathFileEndpoint.zServerLogic { dirName =>
    val result1 = for (dirInfo <- rootFilePathsService.rootPathDirInfo(dirName.fileName)) yield dirInfo
    val result2 = result1.mapError(e => {
      e.printStackTrace()
      ("意外结果", StatusCode.InternalServerError)
    })
    val result3 = for {
      d       <- result2
      dirInfo <- ZIO.fromOption(d)
    } yield dirInfo
    result3.mapError(_ => ("找不到文件", StatusCode.NotFound))
  }

  val rootPathFiles = DesuEndpoint.rootPathFilesEndpoint.zServerLogic { _ =>
    val result1 = for (dirInfo <- rootFilePathsService.rootPathDirName) yield dirInfo
    val result2 = result1.mapError(e => ("意外结果", StatusCode.InternalServerError))
    val result3 = for {
      d       <- result2
      dirInfo <- ZIO.fromOption(d)
    } yield dirInfo
    result3.mapError(_ => ("找不到文件", StatusCode.NotFound))
  }

  val routes = List(baiduPage.widen[ZEnv], rootPathFile.widen[ZEnv], rootPathFiles.widen[ZEnv])
  val docs   = List(DesuEndpoint.baiduPageEndpoint, DesuEndpoint.rootPathFileEndpoint, DesuEndpoint.rootPathFilesEndpoint)

}
