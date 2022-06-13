package zdesu.fusion

import sttp.tapir.ztapir._
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zdesu.endpoint.UserEndpoint
import zdesu.handle.UserHandle

object Fusion {

  val userHttp   = UserEndpoint.userEndpoint.zServerLogic(UserHandle.user)
  val userHttp11 = UserEndpoint.userEndpoint11.zServerLogic(UserHandle.user11)

  val list = List(userHttp, userHttp11)
  val http = ZioHttpInterpreter().toHttp(list)

}
