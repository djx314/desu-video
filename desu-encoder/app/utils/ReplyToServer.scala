package assist.controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait FilesReply {

  val ws: WSClient

  def replyVideo(videoInfo: VideoInfo, targetFiles: List[File]): Future[RequestInfo] = Future {
    val fipeParts = targetFiles.zipWithIndex.map { case (file, index) =>
      FilePart("video_" + index, file.getName, Option("text/plain"), FileIO.fromPath(file.toPath))
    }

    ws.url(videoInfo.returnPath).post(Source(
      fipeParts :::
        DataPart("videoKey", videoInfo.videoKey) ::
        DataPart("videoInfo", videoInfo.videoInfo) ::
        DataPart("returnPath", videoInfo.returnPath) ::
        DataPart("encodeType", videoInfo.encodeType) ::
        Nil))
      .map { wsResult =>
        val resultModel = if (wsResult.status == 200) {
          RequestInfo(true, wsResult.body)
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

@Singleton
class FilesReplyImpl @Inject() (wsClient: WSClient) extends FilesReply {

  override val ws: WSClient = wsClient

}