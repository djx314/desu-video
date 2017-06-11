package utils

import java.io.File
import java.net.URI
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

  def uploadVideo(dateInfo: DateInfo): Future[RequestInfo] = Future {
    //val encoderRequest = DateInfo(2017, 6, 8)
    val sourceFileRoot = new File(videoConfig.sourceRoot)
    val sourceMonthRoot = new File(sourceFileRoot, dateInfo.toYearMonth)
    val mp4File = new File(sourceMonthRoot, dateInfo.toYearMonthDay + ".mp4")
    ws.url(s"${videoConfig.encoderPrefix}encodeHSSW").post(Source(FilePart("video", mp4File.getName, Option("text/plain"), FileIO.fromPath(mp4File.toPath)) :: DataPart("dateInfo", dateInfo.asJson.noSpaces) :: List()))
      .map { wsResult =>
        val resultModel = if (wsResult.status == 200) {
          RequestInfo(true, io.circe.parser.parse(wsResult.body).right.flatMap(_.as[RequestInfo]).right.get.message)
        } else {
          RequestInfo(false, s"请求失败，错误码${wsResult.body}")
        }
        println(resultModel)
        resultModel
      }
  }.flatMap(identity)

}