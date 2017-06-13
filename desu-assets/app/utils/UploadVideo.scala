package utils

import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
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

import scala.concurrent.Future

@Singleton
class UploadVideo @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        ws: WSClient,
                             videoConfig: VideoConfig
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  def uploadVideo(dateInfo: java.util.Date): Future[RequestInfo] = Future {
    //val encoderRequest = DateInfo(2017, 6, 8)
    val sourceFileRoot = new File(videoConfig.sourceRoot)

    val yearMonthFormat = new SimpleDateFormat("yyyyMM")
    val yearMonthStr = yearMonthFormat.format(dateInfo)
    val sourceMonthRoot = new File(sourceFileRoot, yearMonthStr)

    val yearMontDayFormat = new SimpleDateFormat("yyyyMMdd")
    val yearMontDayStr = yearMontDayFormat.format(dateInfo)
    //val sourceFile = new File(sourceMonthRoot, yearMontDayStr + ".mp4")
    //val sourceMonthRoot = new File(sourceFileRoot, dateInfo.toYearMonth)
    val mp4File = new File(sourceMonthRoot, yearMontDayStr + ".mp4")

    val key = s"鹤山新闻${yearMontDayStr}"

    println(s"发送新闻文件:${mp4File.getCanonicalPath},文件是否存在:${mp4File.exists()}")

    ws.url(s"${videoConfig.encoderPrefix}encodeHSSW").post(Source(FilePart("video", mp4File.getName, Option("text/plain"), FileIO.fromPath(mp4File.toPath)) :: DataPart("videoKey", key) :: DataPart("videoInfo", yearMontDayStr) :: DataPart("returnPath", videoConfig.assetsPrefix + "uploadTargetVideo") :: Nil))
      .map { wsResult =>
        val resultModel = if (wsResult.status == 200) {
          RequestInfo(true, wsResult.body)
        } else {
          RequestInfo(false, s"请求失败，错误码${wsResult.body}")
        }
        println(resultModel)
        resultModel
      }
  }.flatMap(identity)

}