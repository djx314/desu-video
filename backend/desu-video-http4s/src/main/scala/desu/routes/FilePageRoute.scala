package desu.routes

import cats.effect._
import desu.config.AppConfig
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

  object NContext1 extends NContext[IO]
  import NContext1._

  val firstRoute = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFiles" =>
    val action = for {
      uri      <- flatMap(IO(uri"http://www.baidu.com"))
      backend  <- resource_use(AsyncHttpClientCatsBackend.resource[IO]())
      request  <- flatMap(IO(basicRequest.get(uri)))
      response <- flatMap(request.send(backend))
      l4       <- flatMap(IO(response.body.merge))
      l5       <- map(Ok(l4, `Content-Type`(MediaType.text.`html`, Charset.`UTF-8`)))
    } yield l5
    action.run(runner)
  }

}
