package zdesu

import zio._

package object mainapp {

  type ProjectEnv = DesuConfig with SlickDBAction

  val zioDB: SlickDBAction.type = SlickDBAction

  object ProjectEnv {
    private val dbLayer                  = zioDB.dbLive >>> zioDB.live
    val layer: RLayer[Scope, ProjectEnv] = dbLayer ++ DesuConfigModel.layer
  }

}
