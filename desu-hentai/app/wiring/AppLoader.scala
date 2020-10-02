package wiring

import akka.stream.Materializer
import assist.controllers._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import com.softwaremill.macwire._
import play.api.routing.SimpleRouter
import tapir_controllers.EncodeTapirLink
import utils._
import utils.tapir.TapirComponentsApplication
import zio.{Runtime, ZEnv, ZIO}
import zio.interop.catz._

import scala.concurrent.ExecutionContext

class InjectedAhcWSComponents(
  override val environment: Environment,
  override val configuration: Configuration,
  override val applicationLifecycle: ApplicationLifecycle,
  override val materializer: Materializer,
  override val executionContext: ExecutionContext
) extends AhcWSComponents

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with NoHttpFiltersComponents with TapirComponentsApplication {

  private implicit def as      = actorSystem
  private implicit lazy val ec = executionContext

  /*
   * Assets 模块配置开始
   */
  private lazy val AssetsConfigurationProvider                                          = wire[_root_.controllers.AssetsConfigurationProvider]
  private def AssetsConfigurationGen(a: _root_.controllers.AssetsConfigurationProvider) = a.get
  private lazy val AssetsConfiguration                                                  = wireWith(AssetsConfigurationGen _)

  private lazy val AssetsMetadataProvider                                     = wire[_root_.controllers.AssetsMetadataProvider]
  private def AssetsMetadataGen(a: _root_.controllers.AssetsMetadataProvider) = a.get
  private lazy val AssetsMetadata                                             = wireWith(AssetsMetadataGen _)

  private lazy val Assets                           = wire[_root_.controllers.Assets]
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
  /*
   * ws 模块配置开始
   */
  private lazy val InjectedAhcWSComponents = wire[InjectedAhcWSComponents]
  private def wsGen(a: AhcWSComponents)    = a.wsClient
  private lazy val ws                      = wireWith(wsGen _)

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
