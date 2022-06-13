package zdesu.mainapp

import zio._
import zhttp.service.Server
import zdesu.fusion.Fusion

object MainApp extends ZIOAppDefault {

  override def run: URIO[ZIOAppArgs with Scope, ExitCode] =
    Server
      .start(8080, Fusion.http)
      .provideLayer(ProjectEnv.live)
      .catchAllDefect(e => ZIO.logErrorCause("启动 http 服务发生错误", Cause.fail(e)))
      .exitCode

}
