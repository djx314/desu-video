package zdesu.mainapp

import zio.*
import zhttp.http.*
import zhttp.service.Server

import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.given
import sttp.tapir.ztapir.given
import zdesu.model.User
import zdesu.service.HelloWorld
import zdesu.fusion.Fusion

object MainApp extends ZIOAppDefault:

  override def run: URIO[ZIOAppArgs with Scope, ExitCode] = Server.start(8090, Fusion.http).exitCode

end MainApp
