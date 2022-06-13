package zdesu.fusion

import sttp.tapir.ztapir._
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zdesu.endpoint.HttpServerRoutingMinimal
import zdesu.handle.UserHandle
import zdesu.mainapp._

object Fusion {

  val rootPathFiles = HttpServerRoutingMinimal.rootPathFiles.zServerLogic(UserHandle.user)
  val rootPathFile  = HttpServerRoutingMinimal.rootPathFile.zServerLogic(UserHandle.rootPathFile)

  val list = List(rootPathFiles.widen[ProjectEnv], rootPathFile.widen[ProjectEnv])
  val http = ZioHttpInterpreter().toHttp(list)

}
