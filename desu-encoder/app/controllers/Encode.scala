package assist.controllers

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import net.scalax.mp4.encoder.CurrentEncode
import org.apache.commons.io.FileUtils
import play.api.libs.circe.Circe

import scala.concurrent.Future

@Singleton
class Encode @Inject() (//assets: CustomAssets,
                        components: ControllerComponents,
                        currentEncode: CurrentEncode,
                        videoEncoders: VideoEncoders,
                        //videoEncode: VideoEncode,
                        videoPathConfig: VideoPathConfig,
                        reply: FilesReply
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  val ffRootFile = new File(videoPathConfig.uploadRoot)

  def encodeRequest = Action.async(parse.multipartFormData(10000000000L)) { implicit request =>

    def encodeVideoFuture(videoInfo: VideoInfo) = {
      val encodeKey = UUID.randomUUID().toString
      val currentRoot = new File(ffRootFile, encodeKey)
      currentRoot.mkdirs()

      val infoFile = new File(currentRoot, videoInfo.videoKey + ".txt")
      val date = new Date()
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      val writeStr = s"${encodeKey}\r\n标题:${videoInfo.videoKey}\r\n时间:${dateFormat.format(date)}"
      FileUtils.writeStringToFile(infoFile, writeStr, "utf-8")

      val sourceDirectory = new File(currentRoot, "source")
      val targetDirectory = new File(currentRoot, "target")
      sourceDirectory.mkdirs()
      targetDirectory.mkdirs()
      val sourceFiles = (0 to videoInfo.videoLength - 1).map { index =>
        val sourceFile = new File(sourceDirectory, "video_" + index)
        request.body.file("video_" + index).map(s => s.ref.moveTo(sourceFile, true))
        sourceFile
      }
      val resultFiles = videoEncoders.encoders.find(_.encodeType == videoInfo.encodeType).get.encode(sourceDirectory, sourceFiles.toList, targetDirectory).flatMap { files =>
        //转码完毕返回用户前去除当前 key
        currentEncode.removeVideoKey(encodeKey)
        reply.replyVideo(videoInfo.copy(videoLength = files.size), files)
      }

      //push 正在编码额视频 key 供查询
      currentEncode.addVideoKey(encodeKey)
      //Future.successful(Ok(RequestInfo(true, sourceFiles.map(_.getCanonicalPath).mkString(",")).asJson))
      sourceFiles.map(_.getCanonicalPath).foreach(println)
      Future.successful(Ok(encodeKey))
    }

    VideoInfo.videoForm.bindFromRequest.fold(
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

  def isEncoding(uuid: String) = Action.async { implicit request =>
    Future successful Ok(currentEncode.keyExists(uuid).toString)
  }

}