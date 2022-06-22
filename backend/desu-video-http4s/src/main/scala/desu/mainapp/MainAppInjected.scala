package desu.mainapp

import desu.config.AppConfig
import desu.endpoint.DesuEndpoint
import desu.routes.AppRoutes
import desu.service.FileFinder
import desu.config.DesuConfigModel

class MainAppInjected:

  lazy val v1 = new AppRoutes(n2, n3)

  private lazy val n2 = new FileFinder(n3)

  private lazy val n3 = new AppConfig(n4)

  private lazy val n4 = new DesuConfigModel()

end MainAppInjected
