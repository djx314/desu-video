package zdesu.handle

import zio._

import zdesu.model.User

import zdesu.service.HelloWorld

object UserHandle:

  def user(i: Unit) =
    val textHmtl = HelloWorld.println.provideLayer(ZLayer.succeed(12) ++ ZLayer.succeed("3234vfddrfs") >>> HelloWorld.live)
    for (t <- textHmtl) yield User(t)

  def user11(i: Unit) =
    val textHmtl = HelloWorld.println.provideLayer(ZLayer.succeed(12) ++ ZLayer.succeed("3234vfddrfs") >>> HelloWorld.live)
    for (t <- textHmtl) yield User(t)

end UserHandle
