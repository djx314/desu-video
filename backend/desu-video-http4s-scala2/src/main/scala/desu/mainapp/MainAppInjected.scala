package desu.mainapp

import desu.config._
import desu.routes.{AppRoutes, IndexPageRoute, StaticPageRoutes}
import desu.service._
import cats.implicits._
import cats.effect._
import cats._
import cats.effect.implicits._
import org.http4s._
import org.http4s.server.Router

object MainAppInjected {

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

                val router1Impl: HttpRoutes[IO] = Router(implicitly[AppConfig].APIRoot -> AppRoutes.build.routes)
                val router1: HttpRoutes[IO]     = Router(implicitly[AppConfig].APPRoot -> router1Impl)
                val router2: HttpRoutes[IO]     = Router(implicitly[AppConfig].APPRoot -> StaticPageRoutes.build.routes)
                val router3: HttpRoutes[IO]     = Router(implicitly[AppConfig].APPRoot -> AssertsHandle.build.staticRoutes)
                val rootRouter: HttpRoutes[IO]  = IndexPageRoute.build.routes
                router1 <+> router2 <+> router3 <+> rootRouter
              }
            )
        )
    )

}
