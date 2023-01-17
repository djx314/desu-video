package desu.mainapp

import desu.config._
import desu.routes.{AppRoutes, AppRoutesImpl}
import desu.service._
import doobie._
import cats.implicits._
import cats.effect._
import cats._
import desu.models.DesuConfig
import cats.effect.implicits._

object MainAppInjected {

  val appRoutes: Resource[IO, AppRoutes] = {
    val injectedResource = new InjectedResource[IO]
    for {
      confImpl      <- injectedResource.desuConfig
      appConfigImpl <- injectedResource.appConfig(confImpl)
      xaImpl        <- injectedResource.doobieXa(confImpl)
    } yield {
      implicit val xa: Transactor[IO]       = xaImpl
      implicit val appConfig: AppConfig     = appConfigImpl
      implicit val desuConfig: DesuConfig   = confImpl
      implicit val fileService: FileService = new FileServiceImpl
      implicit val fileFinder: FileFinder   = new FileFinderImpl

      new AppRoutesImpl: AppRoutes
    }
  }

}

class InjectedResource[F[_]: Async] {

  val desuConfig: Resource[F, DesuConfig]                                   = Resource.eval(DesuConfigBuilder.build.getModel)
  def appConfig(implicit desuConfig: DesuConfig): Resource[F, AppConfig]    = Resource.eval(AppConfig.build.getModel)
  def doobieXa(implicit desuConfig: DesuConfig): Resource[F, Transactor[F]] = DoobieDB.build.transactor

}
