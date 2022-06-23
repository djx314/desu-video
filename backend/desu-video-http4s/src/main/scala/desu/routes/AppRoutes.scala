package desu.routes

import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import cats.syntax.all.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.given
import org.http4s.server.Router
import desu.models.DesuResult
import desu.service.FileFinder
import io.circe.syntax.*
import org.http4s.circe.*
import desu.config.AppConfig
import desu.models.*
import doobie.*

class AppRoutes(using FileFinder, AppConfig):
  private val fileFinder = summon[FileFinder]
  private val appConfig  = summon[AppConfig]

  private val DRoot = appConfig.FilePageRoot

  private given EntityDecoder[IO, RootFileNameRequest] = jsonOf

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> DRoot / "rootPathFiles" =>
    for
      s    <- fileFinder.rootPathFiles
      data <- IO(DesuResult.data(isSucceed = true, s))
      r    <- Ok(data.asJson)
    yield r
  }
  end rootPathFiles

  val rootPathFile = HttpRoutes.of[IO] { case req @ POST -> DRoot / "rootPathFile" =>
    for
      model <- req.as[RootFileNameRequest]
      s     <- fileFinder.rootPathFiles
      data  <- IO(DesuResult.data(isSucceed = true, s))
      r     <- Ok(data.asJson)
    yield r
  }
  end rootPathFile

  private val compatRoutes = rootPathFiles <+> rootPathFile
  val routes               = Router("/" -> compatRoutes).orNotFound

end AppRoutes
