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
import desu.models._
import doobie._

class AppRoutes(fileFinder: FileFinder, fileService: FileService, appConfig: AppConfig) {

  private val DRoot = appConfig.FilePageRoot

  private implicit def jsonOfEncoder: EntityDecoder[IO, RootFileNameRequest] = jsonOf

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> DRoot / "rootPathFiles" =>
    val action = for {
      s    <- fileFinder.rootPathFiles
      data <- IO(DesuResult.data(isSucceed = true, s))
      r    <- Ok(data.asJson)
    } yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }

  val rootPathFile = HttpRoutes.of[IO] { case req @ POST -> DRoot / "rootPathFile" =>
    val action = for {
      model <- req.as[RootFileNameRequest]
      s     <- fileService.rootPathRequestFileId(model.fileName)
      r     <- Ok(s.asJson)
    } yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }

  private val compatRoutes   = rootPathFiles <+> rootPathFile
  val routes: HttpRoutes[IO] = compatRoutes

}

object AppRoutes {
  def build(implicit fileFinder: FileFinder, fileSerice: FileService, appConfig: AppConfig): HttpRoutes[IO] =
    new AppRoutes(implicitly, implicitly, implicitly).routes
}
