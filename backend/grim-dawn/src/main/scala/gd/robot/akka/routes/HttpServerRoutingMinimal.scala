package gd.robot.akka.routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.common.model.DesuResult
import io.circe.syntax._
import akkahttptwirl.TwirlSupport._
import gd.robot.akka.service.FileFinder

import java.nio.file.Paths
import scala.util.{Failure, Success}

class HttpServerRoutingMinimal(fileFinder: FileFinder) {

  val prefix = pathPrefix("api" / "desu")

  val routeWithPath = path("desktopPic") {
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
