package zdesu.fusion

import sttp.tapir.ztapir._
import zio.{Scope, ZIOAppArgs}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zdesu.endpoint.UserEndpoint
import zdesu.handle.UserHandle

object Fusion {

  import zdesu.mainapp._

  val userHttp   = UserEndpoint.userEndpoint.zServerLogic[ProjectEnv](UserHandle.user)
  val userHttp11 = UserEndpoint.userEndpoint11.zServerLogic[ProjectEnv](UserHandle.user11)

  val list = List(userHttp, userHttp11)
  val http = ZioHttpInterpreter().toHttp(list)

}
