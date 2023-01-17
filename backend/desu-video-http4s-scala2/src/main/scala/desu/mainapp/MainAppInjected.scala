package desu.mainapp

import desu.config._
import desu.routes.AppRoutes
import desu.service._
import doobie._
import cats.implicits._
import cats.effect._
import cats._
import cats.effect.implicits._

object MainAppInjected {

  val appRoutes: Resource[IO, AppRoutes] = DesuConfigBuilder.build
    .getResource[IO]
    .flatMap(implicit desuConfig =>
      AppConfig.build
        .getResource[IO]
        .flatMap(implicit appConf =>
          DoobieDB.build.transactorResource[IO].map { implicit xa =>
            implicit val fileService: FileService = FileService.build
            implicit val fileFinder: FileFinder   = FileFinder.build

            AppRoutes.build
          }
        )
    )

}
