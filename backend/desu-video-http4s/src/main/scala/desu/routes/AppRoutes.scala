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

class AppRoutes(using FileFinder, AppConfig):
  private val fileFinder = summon[FileFinder]
  private val appConfig  = summon[AppConfig]

  val DRoot = appConfig.FilePageRoot

  given EntityDecoder[IO, RootFileNameRequest] = jsonOf

  val rootPathFiles = HttpRoutes.of[IO] { case GET -> DRoot / "rootPathFiles" =>
    for
      s    <- fileFinder.rootPathFiles
      data <- IO(DesuResult.data(isSucceed = true, s))
      r    <- Ok(data.asJson)
    yield r
  }

  val rootPathFile = HttpRoutes.of[IO] { case req @ POST -> DRoot / "rootPathFile" =>
    for
      model <- req.as[RootFileNameRequest]
      s     <- fileFinder.rootPathFiles
      data  <- IO(DesuResult.data(isSucceed = true, s))
      r     <- Ok(data.asJson)
    yield r
  }

  val routes = Router("/" -> rootPathFiles).orNotFound

end AppRoutes
