package assist.controllers

import java.io.File
import java.net.URI
import java.util.UUID
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import play.api.libs.circe.Circe
import utils.VideoConfig

import scala.concurrent.Future

@Singleton
class Encode @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        formatFactoryVideoEncode: FormatFactoryVideoEncode,
                        videoEncode: VideoEncode,
                        videoConfig: VideoConfig
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  val ffRootFile = new File(videoConfig.ffmpegRoot)
  //val ffPostSource = new File(ffRootFile, "postSource")
  //val ffSource = new File(ffRootFile, "ffsource")

  import play.api.data._
  import play.api.data.Forms._

  val videoForm = Form(
    mapping(
      "videoKey" -> nonEmptyText,
      "videoInfo" -> nonEmptyText,
      "returnPath" -> nonEmptyText
    )(VideoInfo.apply)(VideoInfo.unapply)
  )

  def encodeRequest = Action.async(parse.multipartFormData(10000000000L)) { implicit request =>
    //val dateInfoStr = request.body.dataParts("dateInfo").head
    //val dateInfo = io.circe.parser.parse(dateInfoStr).right.get.as[DateInfo].right.get

    println("11111111111111111111111111111111111" * 20)
    def encodeVideoFuture(videoInfo: VideoInfo) = {
      val currentRoot = new File(ffRootFile, videoInfo.videoKey)
      val sourceDirectory = new File(currentRoot, "source")
      val targetDirectory = new File(currentRoot, "target")
      sourceDirectory.mkdirs()
      targetDirectory.mkdirs()
      val sourceFile = new File(sourceDirectory, "tobeEncode.mp4")
      val targetFile = new File(targetDirectory, "encoded.mp4")
      request.body.file("video").map(s => s.ref.moveTo(sourceFile, true))
      videoEncode.encodeVideo(videoInfo, sourceFile, targetFile)
      Future.successful(Ok(RequestInfo(true, sourceFile.getCanonicalPath).asJson))
    }

    videoForm.bindFromRequest.fold(
      formWithErrors => {
        println("参数错误")
        Future.successful(BadRequest("错误的参数"))
      },
      videoInfo => {
        println("返回结果:" + videoInfo)
        encodeVideoFuture(videoInfo)
      }
    )
  }

}