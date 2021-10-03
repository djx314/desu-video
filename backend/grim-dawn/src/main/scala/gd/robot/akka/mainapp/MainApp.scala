package gd.robot.akka.mainapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.macwire._
import desu.video.common.slick.DesuDatabase
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.GoHomeKeyListener
import gd.robot.akka.routes.HttpServerRoutingMinimal
import gd.robot.akka.service.{FileFinder, FileService}

import scala.concurrent.Future
import scala.io.StdIn

object MainApp {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

  private lazy val appConfig    = wire[AppConfig]
  private lazy val desuDatabase = wire[DesuDatabase]

  private lazy val fileService = wire[FileService]
  private lazy val fileFinder  = wire[FileFinder]

  lazy val routingMinimal = wire[HttpServerRoutingMinimal]

}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    implicit val system = MainApp.system
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val bindingFuture: Future[ServerBinding] = Http().newServerAt("localhost", 8080).bind(MainApp.routingMinimal.route)

    val goHomeActor = system.systemActorOf(GoHomeKeyListener(bindingFuture), "go-home-key-listener")
    goHomeActor ! GoHomeKeyListener.StartGoHomeKeyListener

    println(s"Server online at http://localhost:8080/\nPress Number1 to stop...")
    /*StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done*/
  }

}
