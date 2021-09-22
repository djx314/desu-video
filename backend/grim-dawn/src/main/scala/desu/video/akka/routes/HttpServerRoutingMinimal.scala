package desu.video.akka.routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.akka.model.FileNotConfirmException
import desu.video.common.model.DesuResult
import io.circe.syntax._
import akkahttptwirl.TwirlSupport._
import gd.robot.akka.service.{FileFinder, FileService}

import java.nio.file.Paths
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
  } ~ path("desktopPic") {
    get {
      extractLog { implicit log =>
        onComplete(fileFinder.getDesktopFile) {
          case Success(path) => getFromFile(path.toFile)
          case Failure(_)    => getFromFile(Paths.get(".", "backend", "grim-dawn", "target", "效果图.png").toFile)
        }
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
