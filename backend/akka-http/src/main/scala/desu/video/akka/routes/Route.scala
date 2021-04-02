package desu.video.akka.routes

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import com.typesafe.config.ConfigFactory

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

import io.circe.syntax._

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

import scala.concurrent.Future
import scala.io.StdIn
import scala.jdk.CollectionConverters._

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val dirPath = Paths.get(ConfigFactory.load().getString("desu.video.file.rootPath"))

    val route = path("hello") {
      get {
        def fileStream = Files.list(dirPath).map(_.toFile.getName)
        def fileList   = Future { (fileStream.collect(Collectors.toList[String]).asScala.to(List), 2) }
        onSuccess(fileList)((list, num) => complete(list.asJson))
      }
    }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
