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

object MainAppInjected:

  object ProEnv1 extends ProjectEnvInjected1
  import ProEnv1.{env => env1, Env => Env1}

  val appRoutes: Resource[IO, AppRoutes] = for
    given ZEnvironment[Env1] <- env1
    given FileService        <- Resource.pure(new FileServiceImpl)
    given FileFinder         <- Resource.pure(new FileFinderImpl)
    route                    <- Resource.pure(new AppRoutesImpl)
  yield route

end MainAppInjected

trait ProjectEnvInjected1:

  type Env = DesuConfig & AppConfig & Transactor[IO]

  def env: Resource[IO, ZEnvironment[Env]] = for
    configModel          <- Resource.pure(new DesuConfigModelImpl)
    given DesuConfig     <- Resource.eval(configModel.configIO)
    given AppConfig      <- Resource.pure(new AppConfigImpl)
    doobieDB             <- Resource.pure(new DoobieDBImpl)
    given Transactor[IO] <- doobieDB.transactor
  yield ZEnvironment(implicitly[DesuConfig], implicitly[Transactor[IO]], implicitly[AppConfig])

end ProjectEnvInjected1

given [ModelTag: Tag, S <: ModelTag](using ZEnvironment[S]): ModelTag = summon[ZEnvironment[S]].get
