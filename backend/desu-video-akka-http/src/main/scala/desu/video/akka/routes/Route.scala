package desu.video.akka.routes

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.akka.mainapp.MainApp
import desu.video.akka.model.RootFileNameRequest
import io.circe.syntax._

import scala.io.StdIn

object HttpServerRoutingMinimal {
  def fileService = MainApp.fileService

  val route = path("rootPathFiles") {
    get {
      onSuccess(fileService.rootPathFiles)(list => complete(list.asJson))
    }
  } ~ path("rootPathFile") {
    post {
      entity(as[RootFileNameRequest]) { fileName =>
        onSuccess(fileService.rootPathRequestFileId(fileName.fileName))(model => complete(model.asJson))
      }
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
