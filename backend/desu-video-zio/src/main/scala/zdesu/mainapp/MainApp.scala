package zdesu.mainapp

import zio._
import zhttp.http._
import zhttp.service.Server

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._
import sttp.tapir.ztapir._
import zdesu.model.User
import zdesu.service.HelloWorld
import zdesu.fusion.Fusion

object MainApp extends ZIOAppDefault {

  override def run: URIO[ZIOAppArgs with Scope, ExitCode] = Server.start(8090, Fusion.http).exitCode

}
