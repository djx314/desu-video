package assist.controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.{FileIO, Source}
import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import org.slf4j.LoggerFactory
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait FilesReply {

  val logger = LoggerFactory.getLogger(classOf[FilesReply])

  val ws: WSClient

  def replyVideo(videoInfo: VideoInfo, targetFiles: List[File]): Future[RequestInfo] =
    Future {
      val fileParts = targetFiles.zipWithIndex.map {
        case (file, index) =>
          FilePart("video_" + index, file.getName, Option("text/plain"), FileIO.fromPath(file.toPath))
      }
      println(fileParts)
      ws.url(videoInfo.returnPath)
        .post(
          Source(
            fileParts :::
              DataPart("videoKey", videoInfo.videoKey) ::
              DataPart("videoInfo", videoInfo.videoInfo) ::
              DataPart("returnPath", videoInfo.returnPath) ::
              DataPart("encodeType", videoInfo.encodeType) ::
              DataPart("videoLength", videoInfo.videoLength.toString) ::
              Nil
          )
        )
        .map { wsResult =>
          val resultModel = if (wsResult.status == 200) {
            RequestInfo(true, wsResult.bodyAsBytes.decodeString("utf-8"))
          } else {
            RequestInfo(false, s"请求失败，错误码${wsResult.bodyAsBytes.decodeString("utf-8")}")
          }
          logger.info(resultModel.toString)
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
