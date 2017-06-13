package assist.controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait FileReply {

  val ws: WSClient

  def replyVideo(videoInfo: VideoInfo, targetFile: File): Future[RequestInfo] = Future {
    ws.url(videoInfo.returnPath).post(Source(FilePart("video", targetFile.getName, Option("text/plain"), FileIO.fromPath(targetFile.toPath)) :: DataPart("videoKey", videoInfo.videoKey) :: DataPart("videoInfo", videoInfo.videoInfo) :: DataPart("returnPath", videoInfo.returnPath) :: Nil))
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
class FileReplyImpl @Inject() (wsClient: WSClient) extends FileReply {

  override val ws: WSClient = wsClient

}