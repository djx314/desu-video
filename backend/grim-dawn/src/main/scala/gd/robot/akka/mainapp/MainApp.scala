package gd.robot.akka.mainapp

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.macwire._
import desu.video.common.slick.DesuDatabase
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.service.FileFinder
import gd.robot.akka.ui.{DelayBuff, DelayBuffUI}
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher, ImageMatcherEnv, ImageUtils}

object MainApp {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

  lazy val imageMatcher   = wire[ImageMatcher]
  lazy val gdSystemUtils  = wire[GDSystemUtils]
  lazy val delayBuffUI    = wire[DelayBuffUI]
  lazy val webappListener = system.systemActorOf(WebAppListener(), "web-app-listener")

  private def delayBuff(name: String)            = wire[DelayBuff]
  private val delayBuffFunc: String => DelayBuff = delayBuff _

  private lazy val appConfig                        = wire[AppConfig]
  private lazy val imageMatcherEnv: ImageMatcherEnv = appConfig.imgMatch
  private lazy val imageUtils                       = wire[ImageUtils]
  private lazy val desuDatabase                     = wire[DesuDatabase]

  private lazy val fileFinder = wire[FileFinder]

}

object GlobalVars {
  lazy val imageMatcher: ImageMatcher                         = MainApp.imageMatcher
  lazy val gdSystemUtils: GDSystemUtils                       = MainApp.gdSystemUtils
  lazy val delayBuffUI: DelayBuffUI                           = MainApp.delayBuffUI
  lazy val webappListener: ActorRef[WebAppListener.GoHomeKey] = MainApp.webappListener
}
