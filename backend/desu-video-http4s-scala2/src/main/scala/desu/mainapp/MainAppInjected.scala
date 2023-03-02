package desu.mainapp

import desu.config._
import desu.routes.{AppRoutes, StaticPageRoutes}
import desu.service._
import cats.implicits._
import cats.effect._
import cats._
import cats.effect.implicits._
import org.http4s._
import org.http4s.server.Router

object MainAppInjected {

  val aa: javax.sound.sampled.spi.FormatConversionProvider = null

  val appRoutes: Resource[IO, HttpRoutes[IO]] = DesuConfigBuilder.build
    .getResource[IO]
    .flatMap(implicit desuConfig =>
      AppConfig.build
        .getResource[IO]
        .flatMap(implicit appConf =>
          DoobieDB.build
            .transactorResource[IO]
            .flatMap(implicit xa =>
              AbcAppRun.resource[IO].map { implicit mp3Context =>
                implicit val fileService: FileService = FileService.build
                implicit val fileFinder: FileFinder   = FileFinder.build

                Router(appConf.FilePageRoot -> AppRoutes.build) <+> Router(appConf.PageRoot -> StaticPageRoutes.build)
              }
            )
        )
    )

}
