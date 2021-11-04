package desu.routes

import cats.effect._
import desu.config.AppConfig
import desu.models.{ResultSet, RootFilePaths}
import desu.number.NContext
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s.headers.`Content-Type`
import sttp.client3._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

class FilePageRoute(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val firstRoute = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFiles" =>
    val action = for {
      uri      <- NContext.pureFlatMap[IO](uri"http://www.baidu.com")
      backend  <- NContext.resource(AsyncHttpClientCatsBackend.resource[IO]())
      request  <- NContext.pureFlatMap[IO](basicRequest.get(uri))
      response <- NContext.flatMap(request.send(backend))
      l4       <- NContext.pureFlatMap[IO](response.body.merge)
      l5       <- NContext.map(Ok(l4, `Content-Type`(MediaType.text.`html`, Charset.`UTF-8`)))
    } yield l5
    action.method1(NContext.runner)
  }

}
