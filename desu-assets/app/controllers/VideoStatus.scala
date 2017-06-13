package assist.controllers

import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Calendar
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
import utils.{UploadVideo, VideoConfig}

import scala.concurrent.Future

@Singleton
class VideoStatus @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        ws: WSClient,
                        videoConfig: VideoConfig
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  case class VideoInfo(isUploaded: Boolean, isEncoded: Boolean, sourceUrl: String, targetUrl: String)

  def searchPage = Action.async { implicit request =>
    Future.successful(Ok(views.html.VideoStatus()))
  }

  def status(dateStr: String) = Action.async { implicit request =>
    val myFmt = new SimpleDateFormat("yyyy-MM-dd")
    val date = myFmt.parse(dateStr)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dateInfo = DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))

    val sourceFileRoot = new File(videoConfig.sourceRoot)
    val sourceMonthRoot = new File(sourceFileRoot, dateInfo.toYearMonth)
    val sourceFile = new File(sourceMonthRoot, dateInfo.toYearMonthDay + ".mp4")

    val targetFileRoot = new File(videoConfig.targetRoot)
    val targetMonthRoot = new File(targetFileRoot, dateInfo.toYearMonth)
    val targetFile = new File(targetMonthRoot, dateInfo.toYearMonthDay + ".mp4")

    val targetMonthFormat = new SimpleDateFormat("yyyyMM")
    val targetDayFormat = new SimpleDateFormat("yyyyMMdd")

    val targetUrl = assist.controllers.routes.Assets.target(targetMonthFormat.format(date) + "/" + targetDayFormat.format(date) + ".mp4").toString
    val sourceUrl = assist.controllers.routes.Assets.source(targetMonthFormat.format(date) + "/" + targetDayFormat.format(date) + ".mp4").toString

    Future successful Ok(VideoInfo(sourceFile.exists(), targetFile.exists(), sourceUrl, targetUrl).asJson)
  }

}