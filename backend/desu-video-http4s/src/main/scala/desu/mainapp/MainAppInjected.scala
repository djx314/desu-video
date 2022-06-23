package desu.mainapp

import desu.config.AppConfig
import desu.endpoint.DesuEndpoint
import desu.routes.AppRoutes
import desu.service.FileFinder
import desu.config.DesuConfigModel
import desu.config.DoobieDB
import doobie.*
import cats.implicits.given
import cats.effect.*
import cats.*
import desu.models.DesuConfig

class MainAppInjected:

  val appRoutes: Resource[IO, AppRoutes] = for
    configModel          <- Resource.pure(new DesuConfigModel)
    given DesuConfig     <- Resource.eval(configModel.configIO)
    given AppConfig      <- Resource.pure(new AppConfig)
    doobieDB             <- Resource.pure(new DoobieDB)
    given Transactor[IO] <- doobieDB.transactor
    given FileFinder     <- Resource.pure(new FileFinder)
    route                <- Resource.pure(new AppRoutes)
  yield route

end MainAppInjected
