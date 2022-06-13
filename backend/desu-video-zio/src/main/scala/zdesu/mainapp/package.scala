package zdesu

import zio._

package object mainapp {

  type ProjectEnv = Scope with ZEnv with DesuConfig with SlickDBAction

  object ProjectEnv {
    val live: RLayer[Scope, ProjectEnv] =
      ZLayer.fromFunction(identity[Scope] _) >+> ZEnv.live ++ DesuConfigModel.layer ++ (SlickDBAction.dbLive >+> SlickDBAction.live)
  }

  val zioDB: SlickDBAction.type = SlickDBAction

}
