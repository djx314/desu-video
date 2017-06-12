package assist.controllers

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.model.{DateInfo, RequestInfo}
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe.syntax._
import io.circe.generic.auto._
import play.api.libs.circe.Circe
import utils.{UploadVideo, VideoConfig}

import scala.concurrent.Future

@Singleton
class Encode @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        ws: WSClient,
                        videoConfig: VideoConfig,
                        uploadVideo: UploadVideo
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  def uploadVideoAction = Action.async(parse.multipartFormData(10000000000L)) { implicit request =>
    val dateInfoStr = request.body.dataParts("dateInfo").head
    val myFmt = new SimpleDateFormat("yyyy-MM-dd")
    val date = myFmt.parse(dateInfoStr)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dateInfo = DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
    val sourceFileRoot = new File(videoConfig.sourceRoot)
    val sourceMonthRoot = new File(sourceFileRoot, dateInfo.toYearMonth)
    sourceMonthRoot.mkdirs()
    val sourceFile = new File(sourceMonthRoot, dateInfo.toYearMonthDay + ".mp4")
    request.body.file("video").map(s => s.ref.moveTo(sourceFile, true))
    uploadVideo.uploadVideo(dateInfo)
    Future.successful(Ok("上传成功"))
  }

  def encodeRequest = Action.async { implicit request =>
    Future.successful(Ok(views.html.UploadVideo()))
  }

  def Index = Action.async { implicit request =>
    Future.successful(Ok(views.html.Index()))
  }

  //转码服务器请求该连接上传转码后的鹤山新闻
  def uploadTargetVideo = Action.async(parse.multipartFormData(10000000000L)) { implicit request =>
    val dateInfoStr = request.body.dataParts("dateInfo").head
    val dateInfo = io.circe.parser.parse(dateInfoStr).right.get.as[DateInfo].right.get

    val targetFileRoot = new File(videoConfig.targetRoot)
    val targetMonthRoot = new File(targetFileRoot, dateInfo.toYearMonth)
    targetMonthRoot.mkdirs()
    val targetFile = new File(targetMonthRoot, dateInfo.toYearMonthDay + ".mp4")
    println(request.body.file("video"))
    request.body.file("video").map(s => s.ref.moveTo(targetFile, true))
    Future.successful(Ok(RequestInfo(true, targetFile.getCanonicalPath).asJson))
  }

  def hardEnode(dateStr: String) = Action.async { implicit request =>
    val myFmt = new SimpleDateFormat("yyyy-MM-dd")
    val date = myFmt.parse(dateStr)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dateInfo = DateInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
    println(dateInfo)
    uploadVideo.uploadVideo(dateInfo)
    Future.successful(Ok("命令发送成功"))
  }

}