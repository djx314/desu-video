package desu.video.akka.routes

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors
import scala.concurrent.Future
import scala.io.StdIn

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val dirPath = Paths.get(ConfigFactory.load().getString("desu.video.file.rootPath"))

    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import scala.jdk.CollectionConverters._
    import io.circe.syntax._

    val route =
      path("hello") {
        get {
          def fileList = Future { Files.list(dirPath).map(_.toFile.getName).collect(Collectors.toList[String]).asScala.to(List) }
          onSuccess(fileList)(list => complete(list.asJson))
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
