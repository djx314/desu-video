package assist.controllers

import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.net.URI
import java.nio.file.StandardCopyOption
import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{RequestInfo, VideoInfo}
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
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class FormatFactoryVideoEncode @Inject() (
                                           fileReply: FileReplyImpl,
                                          videoConfig: VideoConfig
                       ){

  //val ffRootFile = new File(videoConfig.ffmpegRoot)
  //val ffSource = new File(ffRootFile, "ffsource")
  //val ffTarget = new File(ffRootFile, "fftarget")

  val ffmpegSoftBasePath = new File(videoConfig.ffmpegSoftPath)
  val ffmpegExePath = new File(ffmpegSoftBasePath, "FormatFactory.exe")

  val faststartPath = {
    val path1 = new File(ffmpegSoftBasePath, "FFModules")
    val path2 = new File(path1, "Encoder")
    new File(path2, "qt-faststart.exe")
  }

  def encodeVideo(videoInfo: VideoInfo, sourceFile: File, targetFile: File): Future[RequestInfo] = Future {
    //ffmpeg 缓存文件要放在目标文件隔壁
    val targetTempFile = new File(targetFile.getParentFile, UUID.randomUUID().toString + ".mp4")
    val command = s""" "${ffmpegExePath.getCanonicalPath}" "Custom" "customMp4" "${sourceFile.getCanonicalPath}" "${targetTempFile.getCanonicalPath}" """
    val faststartCommand = s""" "${faststartPath.getCanonicalPath}" "${targetTempFile.getCanonicalPath}" "${targetFile.getCanonicalPath}" """

    val execFuture = EncodeHelper.execCommand(command).flatMap { _ =>
      EncodeHelper.waitTargetFileFinishedEncode(targetTempFile)
    }.flatMap { _ =>
      EncodeHelper.execCommand(faststartCommand)
    }

    execFuture.flatMap { _ =>
      fileReply.replyVideo(videoInfo, targetFile)
      /*ws.url(s"${videoConfig.assetsPrefix}uploadTargetVideo").post(Source(FilePart("video", targetFile.getName, Option("text/plain"), FileIO.fromPath(targetFile.toPath)) :: DataPart("dateInfo", dateInfo.asJson.noSpaces) :: List()))
        .map { wsResult =>
          val resultModel = if (wsResult.status == 200) {
            RequestInfo(true, io.circe.parser.parse(wsResult.body).right.flatMap(_.as[RequestInfo]).right.get.message)
          } else {
            RequestInfo(false, s"请求失败，错误码${wsResult.body}")
          }
          println(resultModel)
          resultModel
        }*/
    }

  }.flatMap(identity).recover {
    case e: Exception =>
      e.printStackTrace()
      throw e
  }

}