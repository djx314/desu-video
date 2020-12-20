package wiring

import assist.controllers._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import com.softwaremill.macwire._
import play.api.routing.SimpleRouter
import tapir_controllers.EncodeTapirLink
import utils._
import utils.tapir.TapirComponentsApplication
import zio.{Runtime, ZEnv, ZIO}
import zio.interop.catz._

class AppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents
    with TapirComponentsApplication
    with AhcWSComponents
    with _root_.controllers.AssetsComponents {

  private lazy val archerAssets                     = wire[assist.controllers.Assets]
  private lazy val CommonAssetsController           = wire[assist.controllers.CommonAssetsController]
  private lazy val HentaiConfig: HentaiConfig       = wire[HentaiConfigImpl]
  private lazy val FileUtil: FileUtil               = wire[FileUtilImpl]
  private lazy val Encoder: Encoder                 = wire[Encoder]
  private lazy val EncoderInfoSend: EncoderInfoSend = wire[EncoderInfoSend]
  private lazy val FilesList: FilesList             = wire[FilesList]

  /*
   * Assets 模块配置结束
   */

  implicit val runtime: Runtime[ZEnv] = Runtime.default
  type Eff[A] = ZIO[ZEnv, Throwable, A]

  private val appContextImpl = {
    val commonInstance = new commons.HttpContextImpl(configer = configuration)
    runtime.unsafeRun(commonInstance.live.memoize.toResource[Eff].allocated)
  }

  implicit val appContext: commons.HttpContext = appContextImpl._1

  applicationLifecycle.addStopHook(() => runtime.unsafeRunToFuture(appContextImpl._2))

  // Router
  override lazy val router = {
    val routePrefix: String = "/"
    val route1              = wire[EncodeTapirLink]
    wire[Routes].orElse(SimpleRouter(route1.listRoute))
  }

}
