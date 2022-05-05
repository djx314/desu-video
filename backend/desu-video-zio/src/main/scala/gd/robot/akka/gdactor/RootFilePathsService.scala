package gd.robot.akka.gdactor

import zio._
import zhttp.http._
import zhttp.service.Server

object HelloWorld extends ZIOAppDefault:
  val app = Http.collectZIO[Request](
    _.match
      case Method.GET -> !! / "text" =>
        val textHmtl = HelloWorld1.println.provideLayer(ZLayer.succeed(12) ++ ZLayer.succeed("3234vfddrfs") >>> HelloWorld1.live)
        for (t <- textHmtl) yield Response.json(s"{\"nam33324\": \"$t\"}")
      case Method.GET -> !! / "text11" =>
        val textHmtl = HelloWorld1.println.provideLayer(ZLayer.succeed(12) ++ ZLayer.succeed("3234vfddrfs") >>> HelloWorld1.live)
        for (t <- textHmtl) yield Response.json(s"{\"22\": \"$t\"}")
  )

  override def run: URIO[ZIOAppArgs with Scope, ExitCode] = Server.start(8090, app).exitCode
end HelloWorld
