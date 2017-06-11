package assist.controllers

import java.io.File
import java.net.URI
import java.util.UUID
import javax.inject.{Inject, Singleton}

import net.scalax.mp4.model.{DateInfo, RequestInfo}
import net.scalax.mp4.play.CustomAssets
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import play.api.libs.circe.Circe

import scala.concurrent.Future

@Singleton
class Encode @Inject() (assets: CustomAssets,
                        components: ControllerComponents,
                        configure: Configuration,
                        videoEncode: VideoEncode
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  val ffmpegRoot = {
    configure.get[String]("djx314.path.base.ffmpeg")
  }
  val ffRootFile = new File(ffmpegRoot)
  val ffPostSource = new File(ffRootFile, "postSource")
  val ffSource = new File(ffRootFile, "ffsource")

  def encodeRequest = Action.async(parse.multipartFormData(10000000000L)) { implicit request =>
    val dateInfoStr = request.body.dataParts("dateInfo").head
    val dateInfo = io.circe.parser.parse(dateInfoStr).right.get.as[DateInfo].right.get
    val sourceMonthFile = new File(ffSource, dateInfo.toYearMonth)
    sourceMonthFile.mkdirs()
    val sourceFile = new File(sourceMonthFile, dateInfo.toYearMonthDay + ".mp4")
    request.body.file("video").map(s => s.ref.moveTo(sourceFile, true))
    videoEncode.encodeVideo(dateInfo)
    Future.successful(Ok(RequestInfo(true, sourceFile.getCanonicalPath).asJson))
  }

}