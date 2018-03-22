package assist.controllers

import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.{ Date, UUID }
import javax.inject.{ Inject, Singleton }

import archer.controllers.CommonController
import net.scalax.mp4.model.VideoInfo
import play.api.mvc.{ ControllerComponents, InjectedController }
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import net.scalax.mp4.encoder.CurrentEncode
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.circe.Circe

import scala.concurrent.Future
import scala.util.matching.Regex

@Singleton
class Encode @Inject() (
  currentEncode: CurrentEncode,
  videoEncoders: VideoEncoders,
  videoPathConfig: VideoPathConfig,
  reply: FilesReply,
  controllerComponents: ControllerComponents) extends CommonController(controllerComponents) with Circe {

  implicit def ec = defaultExecutionContext

  val ffRootFile = new File(videoPathConfig.uploadRoot)

  def encodeRequest = Action.async(parse.multipartFormData(Long.MaxValue)) { implicit request =>

    println(request.headers)
    println(request.contentType)

    def encodeVideoFuture(videoInfo: VideoInfo) = {
      val encodeDateTimeFormat = DateTimeFormat.forPattern("yyyy年MM月dd日HH时mm分ss秒SSS")
      val encodeTime = DateTime.now
      val encoderTimeStr = encodeTime.toString(encodeDateTimeFormat)

      val sourceFilesWithName = (0 to videoInfo.videoLength - 1).map { index =>
        request.body.file("video_" + index).map {
          s =>
            println(s.filename)
            s
        }.toList
      }.flatten

      val temFiles = sourceFilesWithName
      val uuid = UUID.randomUUID().toString

      val dirName = encoderTimeStr + "-" + uuid
      val currentRoot = new File(ffRootFile, dirName)
      currentRoot.mkdirs()

      val infoFile = new File(currentRoot, videoInfo.videoKey + ".txt")
      val writeStr = s"${dirName}\r\n标题:${videoInfo.videoKey}\r\n时间:${encoderTimeStr}"
      FileUtils.writeStringToFile(infoFile, writeStr, "utf-8")

      val sourceDirectory = new File(currentRoot, "source")
      val targetDirectory = new File(currentRoot, "target")
      sourceDirectory.mkdirs()
      targetDirectory.mkdirs()

      val sourceFiles = temFiles.zipWithIndex.map {
        case (tempFile, index) =>
          val sourcePath = Paths.get(sourceDirectory.toPath.toString, s"video_$index" /*tempFile.filename.replaceAllLiterally("?", "")*/ )
          tempFile.ref.moveTo(sourcePath, true)
          sourcePath.toFile
      }

      //push 正在编码额视频 key 供查询
      currentEncode.addVideoKey(dirName)

      val resultFiles = videoEncoders.encoders.find(_.encodeType == videoInfo.encodeType).get.encode(videoInfo.videoInfo, sourceDirectory, sourceFiles.toList, targetDirectory).flatMap { files: List[File] =>
        reply.replyVideo(videoInfo.copy(videoLength = files.size), files)
      }.andThen {
        case _ =>
          //转码完毕返回用户后去除当前 key
          currentEncode.removeVideoKey(dirName)
      }
      //Future.successful(Ok(RequestInfo(true, sourceFiles.map(_.getCanonicalPath).mkString(",")).asJson))
      sourceFiles.map(_.getCanonicalPath).foreach(println)
      Future.successful(Ok(dirName))
    }

    VideoInfo.videoForm.bindFromRequest.fold(
      formWithErrors => {
        println("参数错误")
        Future.successful(BadRequest("错误的参数"))
      },
      videoInfo => {
        println("返回结果:" + videoInfo)
        encodeVideoFuture(videoInfo)
      })
  }

  def isEncoding(uuid: String) = Action.async { implicit request =>
    Future successful Ok(currentEncode.keyExists(uuid).toString)
  }

}