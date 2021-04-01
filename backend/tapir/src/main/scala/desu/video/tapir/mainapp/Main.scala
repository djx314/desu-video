package desu.video.tapir.mainapp

import desu.video.tapir.endpoints.AppRoutes

import scala.concurrent.duration._

object Main extends zio.App {

  import zio.interop.catz._
  import org.http4s.server.blaze._
  import org.http4s.implicits._
  import zio._

  override def run(args: List[String]): URIO[ZEnv, zio.ExitCode] = {
    val action1: RIO[Layers.AppContext, Unit] = ZIO.runtime[Layers.AppContext].flatMap { implicit runtime =>
      val builder  = BlazeServerBuilder[Layers.ZIOEnvTask](runtime.platform.executor.asEC)
      val builder2 = builder.bindHttp(8080, "19.125.1.24").withHttpApp(AppRoutes.routes.orNotFound).withIdleTimeout(10.minutes).withResponseHeaderTimeout(10.minutes)
      builder2.serve.compile.drain
    }
    action1.provideCustomLayer(Layers.appLayer).exitCode
  }

}
