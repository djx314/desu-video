package desu.routes

import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import cats.syntax.all.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.given
import org.http4s.server.Router
import desu.models.DesuResult
import desu.service.{FileFinder, FileService}
import io.circe.syntax.*
import org.http4s.circe.*
import desu.config.AppConfig
import desu.models.*
import doobie.*

trait AppRoutes(fileFinder: FileFinder, fileService: FileService, appConfig: AppConfig):

  private val DRoot = appConfig.FilePageRoot

  private given EntityDecoder[IO, RootFileNameRequest] = jsonOf

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> DRoot / "rootPathFiles" =>
    val action = for
      s    <- fileFinder.rootPathFiles
      data <- IO(DesuResult.data(isSucceed = true, s))
      r    <- Ok(data.asJson)
    yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }
  end rootPathFiles

  val rootPathFile = HttpRoutes.of[IO] { case req @ POST -> DRoot / "rootPathFile" =>
    val action = for
      model <- req.as[RootFileNameRequest]
      s     <- fileService.rootPathRequestFileId(model.fileName)
      r     <- Ok(s.asJson)
    yield r

    action.onError(s => IO.blocking(s.printStackTrace))
  }
  end rootPathFile

  private val compatRoutes = rootPathFiles <+> rootPathFile
  val routes               = Router("/" -> compatRoutes).orNotFound

end AppRoutes

class AppRoutesImpl(using FileFinder, FileService, AppConfig) extends AppRoutes(summon, summon, summon)
