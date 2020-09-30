package wiring

import akka.stream.Materializer
import assist.controllers._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import com.softwaremill.macwire._
import net.scalax.mp4.encoder._
import utils.VideoConfig

import scala.concurrent.ExecutionContext

class InjectedAhcWSComponents(
  override val environment: Environment,
  override val configuration: Configuration,
  override val applicationLifecycle: ApplicationLifecycle,
  override val materializer: Materializer,
  override val executionContext: ExecutionContext
) extends AhcWSComponents

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with NoHttpFiltersComponents {

  private implicit def as      = actorSystem
  private implicit lazy val ec = executionContext

  /**
    * Assets 模块配置开始
    */
  private lazy val AssetsConfigurationProvider                                          = wire[_root_.controllers.AssetsConfigurationProvider]
  private def AssetsConfigurationGen(a: _root_.controllers.AssetsConfigurationProvider) = a.get
  private lazy val AssetsConfiguration                                                  = wireWith(AssetsConfigurationGen _)

  private lazy val AssetsMetadataProvider                                     = wire[_root_.controllers.AssetsMetadataProvider]
  private def AssetsMetadataGen(a: _root_.controllers.AssetsMetadataProvider) = a.get
  private lazy val AssetsMetadata                                             = wireWith(AssetsMetadataGen _)

  private lazy val Assets = wire[_root_.controllers.Assets]
  private lazy val CustomAssets = {
    lazy val CustomAssetsMetadata = wire[net.scalax.mp4.play.CustomAssetsMetadata]
    wire[net.scalax.mp4.play.CustomAssets]
  }
  private lazy val archerAssets                 = wire[assist.controllers.Assets]
  private lazy val Encode                       = wire[assist.controllers.Encode]
  private lazy val VideoEncoders: VideoEncoders = wire[VideoEncodersImpl]

  private lazy val FFmpegEncoder: FFmpegEncoder = wire[FFmpegEncoderImpl]

  private lazy val FFmpegEncoderWithAss: FFmpegEncoderWithAss = wire[FFmpegEncoderWithAssImpl]
  private lazy val OgvEncoder: OgvEncoder                     = wire[OgvEncoderImpl]
  private lazy val Mp4Execution: Mp4Execution                 = wire[Mp4Execution]

  private lazy val VideoPathConfig: VideoPathConfig with CurrentEncode with FFConfig = wire[VideoConfig]
  private lazy val FilesReply: FilesReply                                            = wire[FilesReplyImpl]

  /**
    * Assets 模块配置结束
    */
  /**
    * ws 模块配置开始
    */
  private lazy val InjectedAhcWSComponents = wire[InjectedAhcWSComponents]
  private def wsGen(a: AhcWSComponents)    = a.wsClient
  private lazy val ws                      = wireWith(wsGen _)

  /**
    * ws 模块配置结束
    */
  /*private lazy val Index = wire[Index]

  private lazy val WsbsTest = wire[WsbsTest]

  private lazy val PatentModelService = wire[PatentModelService]
  private lazy val Patent = wire[Patent]


  private lazy val PatentComparison = wire[PatentComparison]
  private lazy val PatentComparisonServices = wire[PatentComparisonServices]

  private lazy val Task = wire[Task]
  private lazy val PageTaskServices = wire[PageTaskServices]

  private lazy val PatentStatisticServices = wire[PatentStatisticServices]
  private lazy val PatentStatistic = wire[PatentStatistic]

  private lazy val PatentTempServices = wire[PatentTempServices]
  private lazy val PatentTemp = wire[PatentTemp]

  private lazy val HtmlTemplate = wire[HtmlTemplate]
  private lazy val CacheTimeUpdater = wire[CacheTimeUpdater]*/

  // Router
  override lazy val router = {
    val routePrefix: String = "/"
    wire[Routes]
  }

}
