package assist.controllers

import java.io.File
import java.net.URI
import java.nio.file.StandardCopyOption
import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{DateInfo, RequestInfo}
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import play.api.libs.circe.Circe
import play.api.mvc.MultipartFormData.{DataPart, FilePart}
import utils.VideoConfig

import scala.concurrent.Future

@Singleton
class VideoEncode @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        ws: WSClient,
                             videoConfig: VideoConfig
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  val ffRootFile = new File(videoConfig.ffmpegRoot)
  val ffTarget = new File(ffRootFile, "fftarget")
  val ffSource = new File(ffRootFile, "ffsource")

  def encodeVideo(dateInfo: DateInfo): Future[RequestInfo] = Future {
    val sourceMonthFile = new File(ffSource, dateInfo.toYearMonth)
    val targetMonthFile = new File(ffTarget, dateInfo.toYearMonth)
    sourceMonthFile.mkdirs()
    targetMonthFile.mkdirs()
    val sourceFile = new File(sourceMonthFile, dateInfo.toYearMonthDay + ".mp4")
    val targetFile = new File(targetMonthFile, dateInfo.toYearMonthDay + ".mp4")
    java.nio.file.Files.copy(sourceFile.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)

    ws.url(s"${videoConfig.assetsPrefix}uploadTargetVideo").post(Source(FilePart("video", targetFile.getName, Option("text/plain"), FileIO.fromPath(targetFile.toPath)) :: DataPart("dateInfo", dateInfo.asJson.noSpaces) :: List()))
      .map { wsResult =>
        val resultModel = if (wsResult.status == 200) {
          RequestInfo(true, io.circe.parser.parse(wsResult.body).right.flatMap(_.as[RequestInfo]).right.get.message)
        } else {
          RequestInfo(false, s"请求失败，错误码${wsResult.body}")
        }
        println(resultModel)
        resultModel
      }
  }.flatMap(identity).recover {
    case e: Exception =>
      e.printStackTrace()
      throw e
  }

}