/*package assist.controllers

import java.io.File
import java.nio.file.StandardCopyOption
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.model.{RequestInfo, VideoInfo}
import utils.VideoConfig

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class VideoEncode @Inject() (
                             videoConfig: VideoConfig,
                             fileReply: FileReplyImpl
                       ) {

  val ffRootFile = new File(videoConfig.ffmpegRoot)
  //val ffTarget = new File(ffRootFile, "fftarget")
  //val ffSource = new File(ffRootFile, "ffsource")

  def encodeVideo(videoInfo: VideoInfo, sourceFile: File, targetFile: File): Future[RequestInfo] = Future {
    //val sourceMonthFile = new File(ffSource, dateInfo.toYearMonth)
    //val targetMonthFile = new File(ffTarget, dateInfo.toYearMonth)
    //sourceMonthFile.mkdirs()
    //targetMonthFile.mkdirs()
    //val sourceFile = new File(sourceMonthFile, dateInfo.toYearMonthDay + ".mp4")
    //val targetFile = new File(targetMonthFile, dateInfo.toYearMonthDay + ".mp4")
    java.nio.file.Files.copy(sourceFile.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)

    /*ws.url(s"${videoConfig.encoderPrefix}encodeHSSW").post(Source(FilePart("video", mp4File.getName, Option("text/plain"), FileIO.fromPath(mp4File.toPath)) :: DataPart("videoKey", key) :: DataPart("videoInfo", yearMontDayStr) :: DataPart("returnPath", videoConfig.assetsPrefix + "uploadTargetVideo") :: Nil))
      .map { wsResult =>
        val resultModel = if (wsResult.status == 200) {
          RequestInfo(true, wsResult.body)
        } else {
          RequestInfo(false, s"请求失败，错误码${wsResult.body}")
        }
        println(resultModel)
        resultModel
      }
  }.flatMap(identity)*/

    println(videoInfo.returnPath)
    fileReply.replyVideo(videoInfo, targetFile)
  }.flatMap(identity).recover {
    case e: Exception =>
      e.printStackTrace()
      throw e
  }

}*/
