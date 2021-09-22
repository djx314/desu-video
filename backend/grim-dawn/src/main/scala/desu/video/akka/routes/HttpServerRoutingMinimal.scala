package desu.video.akka.routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.akka.model.{FileNotConfirmException, RootFileNameRequest}
import desu.video.akka.service.{FileFinder, FileService}
import desu.video.common.model.DesuResult
import io.circe.syntax._
import akkahttptwirl.TwirlSupport._

import scala.util.{Failure, Success}

class HttpServerRoutingMinimal(fileFinder: FileFinder, fileService: FileService) {

  val prefix = pathPrefix("api" / "desu")

  val routeWithPath = path("callRobot") {
    get {
      extractLog { implicit log =>
        onComplete(fileService.callRobot) {
          case Success(list)                       => complete(DesuResult.data(true, list))
          case Failure(FileNotConfirmException(_)) => complete(DesuResult.message(false, message = "根目录配置错误或配置已过时"))
          case Failure(_)                          => complete(DesuResult.message(false, message = "未知错误，请联系管理员"))
        }
      }
    }
  } ~ path("rootPathFile") {
    // 未调整
    post {
      entity(as[RootFileNameRequest]) { fileName =>
        onSuccess(fileService.callRobot)(model => complete(model.asJson))
      }
    }
  }

  val apiRoute = path("api") {
    get {
      complete(views.html.api.API())
    }
  }

  val route = prefix(routeWithPath) ~ apiRoute

}
