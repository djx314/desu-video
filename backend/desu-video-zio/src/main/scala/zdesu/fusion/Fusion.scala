package zdesu.fusion

import sttp.tapir.ztapir._
import zio.{Scope, ZIOAppArgs}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zdesu.endpoint.UserEndpoint
import zdesu.handle.UserHandle

object Fusion {

  val userHttp   = UserEndpoint.userEndpoint.zServerLogic[ZIOAppArgs with Scope](UserHandle.user)
  val userHttp11 = UserEndpoint.userEndpoint11.zServerLogic[ZIOAppArgs with Scope](UserHandle.user11)

  val list = List(userHttp, userHttp11)
  val http = ZioHttpInterpreter().toHttp(list)

}
