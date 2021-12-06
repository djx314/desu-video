package gd.robot.akka.mainapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.macwire._
import desu.video.common.slick.DesuDatabase
import gd.robot.akka.config.AppConfig
import gd.robot.akka.service.FileFinder
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher, ImageMatcherEnv, ImageUtils}

object MainApp {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

  lazy val imageMatcher  = wire[ImageMatcher]
  lazy val gdSystemUtils = wire[GDSystemUtils]

  private lazy val appConfig                        = wire[AppConfig]
  private lazy val imageMatcherEnv: ImageMatcherEnv = appConfig.imgMatch
  private lazy val imageUtils                       = wire[ImageUtils]
  private lazy val desuDatabase                     = wire[DesuDatabase]

  private lazy val fileFinder = wire[FileFinder]

}

object GlobalVars {
  lazy val imageMatcher: ImageMatcher   = MainApp.imageMatcher
  lazy val gdSystemUtils: GDSystemUtils = MainApp.gdSystemUtils
}

/*object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    implicit val system = MainApp.system
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val bindingFuture: Future[ServerBinding] = Http().newServerAt("localhost", 8080).bind(GlobalVars.routingMinimal.route)

    val webappListener = system.systemActorOf(WebAppListener(), "web-app-listener")
    webappListener ! WebAppListener.StartGoHomeKeyListener

    println(s"Server online at http://localhost:8080/\nPress Number8 to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}*/
