package assist.controllers

import java.io.File
import java.net.URI
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
                        ws: WSClient
                       ) extends AbstractController(components) with Circe {

  implicit val ec = defaultExecutionContext

  val encoderPrefix = {
    configure.get[String]("djx314.url.server.encoder")
  }
  val assetsPrefix = {
    configure.get[String]("djx314.url.server.asset")
  }

  val targetRoot = {
    configure.get[String]("djx314.path.base.target")
  }
  val sourceRoot = {
    configure.get[String]("djx314.path.base.source")
  }

  def encodeRequest = Action.async { implicit request =>
    val encoderRequest = DateInfo(2017, 6, 8)
    val sourceFileRoot = new File(sourceRoot)
    val sourceMonthRoot = new File(sourceFileRoot, encoderRequest.toYearMonth)
    val mp4File = new File(sourceMonthRoot, encoderRequest.toYearMonthDay + ".mp4")
    ws
      .url(s"${encoderPrefix}encodeHSSW")
      .withQueryStringParameters("dateInfo" -> encoderRequest.asJson.noSpaces)
      .post(mp4File)
      .map { wsResult =>
      val resultModel = if (wsResult.status == 200) {
        RequestInfo(true, "请求成功")
      } else {
        RequestInfo(false, s"请求失败，错误码${wsResult.statusText}")
      }
      Ok(resultModel.asJson)
    }
  }

}