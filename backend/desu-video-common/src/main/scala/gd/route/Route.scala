package gd.route

import sttp.model.Uri
import sttp.tapir._
import sttp.tapir.client.sttp.{SttpClientInterpreter, WebSocketToPipe}

object Route {

  val prefix = endpoint.in("api" / "desu")

  def toUri[I, E, O, R, T](e: Endpoint[I, E, O, R])(value: I)(implicit wsToPipe: WebSocketToPipe[R]): Uri = {
    val r = SttpClientInterpreter().toRequestThrowDecodeFailures(e, Option.empty)
    r(value).uri
  }

  val callRobotEndpoint = prefix.get.in("callRobot")
  val callRobotUri      = toUri(callRobotEndpoint)(()).toString()

  val desktopPicEndpoint = prefix.get.in("desktopPic")
  val desktopUri         = toUri(desktopPicEndpoint)(()).toString()

}
