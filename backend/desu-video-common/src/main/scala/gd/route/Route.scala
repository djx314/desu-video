package gd.route

import sttp.tapir._
import sttp.tapir.client.sttp.SttpClientInterpreter

object Route {
  val prefix = endpoint.in("api" / "desu")

  object inner {
    val callRobotEndpoint = prefix.get.in("callRobot")
    val callRobotRequest  = SttpClientInterpreter().toRequestThrowDecodeFailures(callRobotEndpoint, Option.empty)

    val desktopPicEndpoint = prefix.get.in("desktopPic")
    val desktopPicRequest  = SttpClientInterpreter().toRequestThrowDecodeFailures(desktopPicEndpoint, Option.empty)
  }

  val callRobotUri = inner.callRobotRequest(()).uri.toString()
  val desktopUri   = inner.desktopPicRequest(()).uri.toString()
}
