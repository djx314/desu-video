package desu.routes

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import cats.syntax.all._
import org.http4s.implicits._
import org.http4s.server.Router
import desu.models.DesuResult
import desu.service.{FileFinder, FileService}
import io.circe.syntax._
import org.http4s.circe._
import desu.config.AppConfig
import desu.mainapp.AAb
import desu.models._
import doobie._
import org.http4s.headers._

class AppRoutes(fileFinder: FileFinder, fileService: FileService, appConfig: AppConfig, mp3Context: AAb) {

  private implicit def jsonOfEncoder: EntityDecoder[IO, RootFileNameRequest] = jsonOf

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> Root / "rootPathFiles" =>
    val action = for {
      s    <- fileFinder.rootPathFiles
      data <- IO(DesuResult.data(isSucceed = true, s))
      r    <- Ok(data.asJson)
    } yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }

  val rootPathFile = HttpRoutes.of[IO] { case req @ POST -> Root / "rootPathFile" =>
    val action = for {
      model <- req.as[RootFileNameRequest]
      s     <- fileService.rootPathRequestFileId(model.fileName)
      r     <- Ok(s.asJson)
    } yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }

  val htmlPage = HttpRoutes.of[IO] { case req @ GET -> Root / "aaaa.html" =>
    Ok(
      """
        |<!DOCTYPE html>
        |<html>
        |<head>
        |<meta charset="utf-8">
        |<title>文档标题</title>
        |</head>
        |
        |<body>
        |<audio controls>
        |  <source src="./uu.wav" type="audio/wav">
        |    您的浏览器不支持 audio 元素。
        |</audio>
        |
        |</body>
        |
        |</html>
        |""".stripMargin,
      Headers(`Content-Type`(MediaType.text.html))
    )
  }

  val newPathFile = HttpRoutes.of[IO] { case req @ GET -> Root / "uu.wav" =>
    Ok(mp3Context.action[IO], Headers(`Content-Type`(MediaType.audio.wav)))
  }

  def routes: HttpRoutes[IO] = rootPathFiles <+> rootPathFile <+> htmlPage <+> newPathFile

}

object AppRoutes {
  def build(implicit fileFinder: FileFinder, fileSerice: FileService, appConfig: AppConfig, mp3Context: AAb): AppRoutes =
    new AppRoutes(implicitly, implicitly, implicitly, implicitly)
}
