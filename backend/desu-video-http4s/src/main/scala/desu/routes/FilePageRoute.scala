package desu.routes

import cats.effect._

import desu.config.AppConfig
import desu.models.{ResultSet, RootFilePaths}

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import io.circe.syntax._

class FilePageRoute(appConfig: AppConfig) {

  val FilePageRoot = appConfig.FilePageRoot

  val firstRoute = HttpRoutes.of[IO] { case GET -> FilePageRoot / "rootPathFiles" =>
    Ok(ResultSet(RootFilePaths(List("aa", "bb", "cc"))).asJson)
  }

}
