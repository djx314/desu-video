package desu.mainapp

import desu.config.*
import desu.endpoint.DesuEndpoint
import desu.routes.{AppRoutes, AppRoutesImpl}
import desu.service.*
import doobie.*
import cats.implicits.given
import cats.effect.*
import cats.*
import desu.models.DesuConfig
import zio.{IO as _, *}
import cats.effect.cps.*
import cats.effect.implicits.given
import language.experimental.fewerBraces

object MainAppInjected:

  object ProEnv1 extends InjectedModule1
  import ProEnv1.{env as env1, Env as Env1}

  val appRoutes: Resource[IO, AppRoutes] = async[Resource[IO, *]] {
    given ZEnvironment[Env1] = env1.await
    given FileService        = new FileServiceImpl
    given FileFinder         = new FileFinderImpl
    new AppRoutesImpl
  }

end MainAppInjected

trait InjectedModule1:

  type Env = DesuConfig & AppConfig & Transactor[IO]

  val env: Resource[IO, ZEnvironment[Env]] = async[Resource[IO, *]] {
    val configModel      = new DesuConfigModelImpl
    given DesuConfig     = Resource.eval(configModel.configIO).await
    given AppConfig      = new AppConfigImpl
    val doobieDB         = new DoobieDBImpl
    given Transactor[IO] = doobieDB.transactor.await
    ZEnvironment(implicitly[DesuConfig], implicitly[Transactor[IO]], implicitly[AppConfig])
  }

end InjectedModule1

given [ModelTag: Tag, S <: ModelTag](using ZEnvironment[S]): ModelTag = summon[ZEnvironment[S]].get
