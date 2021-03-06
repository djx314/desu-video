package desu.video.akka.routes

import akka.http.scaladsl.server.Directives.*
import desu.video.akka.model.{FileNotConfirmException, RootFileNameRequest}
import desu.video.akka.service.{FileFinder, FileService}
import desu.video.common.model.DesuResult
import de.heikoseeberger.akkahttpziojson.ZioJsonSupport._

import scala.util.{Failure, Success}

class HttpServerRoutingMinimal(fileFinder: FileFinder, fileService: FileService) {

  val prefix = pathPrefix("api" / "desu")

  val routeWithPath = path("rootPathFiles") {
    get {
      extractLog { implicit log =>
        onComplete(fileFinder.rootPathFiles) {
          case Success(list) =>
            complete(DesuResult.data(true, list))
          case Failure(FileNotConfirmException(_)) => complete(DesuResult.message(false, message = "根目录配置错误或配置已过时"))
          case Failure(_)                          => complete(DesuResult.message(false, message = "未知错误，请联系管理员"))
        }
      }
    }
  } ~ path("rootPathFile") {
    // 未调整
    post {
      entity(as[RootFileNameRequest]) { fileName =>
        onSuccess(fileService.rootPathRequestFileId(fileName.fileName))(model => complete(model))
      }
    }
  }

  val route = prefix(routeWithPath)

}
