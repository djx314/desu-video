package zdesu

import zio._

package object mainapp {

  type ProjectEnv = ZEnv with DesuConfig with Scope

  object ProjectEnv {
    val live: RLayer[Scope, ProjectEnv] = ZLayer.fromFunction(identity[Scope] _) >+> ZEnv.live ++ DesuConfigModel.layer
  }

}
