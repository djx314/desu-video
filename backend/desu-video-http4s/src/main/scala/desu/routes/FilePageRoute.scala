package desu.routes

import cats.effect._
import desu.config.AppConfig
import desu.number.CollectContext
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s.headers.`Content-Type`
import sttp.client3._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import desu.service.RootFilePathsService

class FilePageRoute(appConfig: AppConfig, rootFilePathsService: RootFilePathsService) {

  val FilePageRoot = appConfig.FilePageRoot

  object nctx extends CollectContext[IO]
  import nctx._

  val baiduPage = HttpRoutes.of[IO] { case GET -> FilePageRoot / "baiduPage" =>
    val action = for {
      uri      <- flatMap(IO(uri"http://www.baidu.com"))
      backend  <- resource_use(AsyncHttpClientCatsBackend.resource[IO]())
      request  <- flatMap(IO(basicRequest.get(uri)))
      response <- flatMap(request.send(backend))
      l4       <- flatMap(IO(response.body.merge))
      l5       <- map(Ok(l4, `Content-Type`(MediaType.text.`html`, Charset.`UTF-8`)))
    } yield l5
    runF(action)
  }

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFiles" / dirName =>
    val action = for {
      dirInfo <- flatMap(rootFilePathsService.rootPathDirInfo(dirName))
      result  <- map(Ok(dirInfo.asJson))
    } yield result
    runF(action)
  }

}
