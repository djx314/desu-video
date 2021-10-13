package gd.robot.akka.mainapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.macwire._
import desu.video.common.slick.DesuDatabase
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.routes.HttpServerRoutingMinimal
import gd.robot.akka.service.FileFinder
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher, ImageMatcherEnv, ImageUtils}

import scala.concurrent.Future

object MainApp {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

  lazy val imageMatcher  = wire[ImageMatcher]
  lazy val gdSystemUtils = wire[GDSystemUtils]

  private lazy val appConfig                        = wire[AppConfig]
  private lazy val imageMatcherEnv: ImageMatcherEnv = appConfig.imgMatch
  private lazy val imageUtils                       = wire[ImageUtils]
  private lazy val desuDatabase                     = wire[DesuDatabase]

  private lazy val fileFinder = wire[FileFinder]

  lazy val routingMinimal = wire[HttpServerRoutingMinimal]

}

object GlobalVars {
  lazy val imageMatcher: ImageMatcher               = MainApp.imageMatcher
  lazy val routingMinimal: HttpServerRoutingMinimal = MainApp.routingMinimal
  lazy val gdSystemUtils: GDSystemUtils             = MainApp.gdSystemUtils
}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    implicit val system = MainApp.system
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val bindingFuture: Future[ServerBinding] = Http().newServerAt("localhost", 8080).bind(GlobalVars.routingMinimal.route)

    system.systemActorOf(WebAppListener(bindingFuture), "web-app-listener")

    println(s"Server online at http://localhost:8080/\nPress Number8 to stop...")
    /*StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done*/
  }

}
