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

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "gd-system")

  lazy val imageMatcher   = wire[ImageMatcher]
  lazy val gdSystemUtils  = wire[GDSystemUtils]
  def delayBuffUI         = wire[DelayBuffUI]
  lazy val webappListener = system.systemActorOf(WebAppListener(), "web-app-listener")

  private lazy val appConfig                        = wire[AppConfig]
  private lazy val imageMatcherEnv: ImageMatcherEnv = appConfig.imgMatch
  private lazy val imageUtils                       = wire[ImageUtils]
  private lazy val desuDatabase                     = wire[DesuDatabase]
  private lazy val delayBuff                        = () => wire[DelayBuff]

  private lazy val fileFinder = wire[FileFinder]

}

object GlobalVars {
  import GlobalVarsInject._
  def apply[T](implicit f: GF[T]): T = f.value
}

object GlobalVarsInject {
  type GF[T] = GlobalVarsFetch[T, GlobalVarsInject.type]
  def GF[T](t: T): GF[T] = new GlobalVarsFetch(t)

  implicit val imageMatcherImplicit: GF[ImageMatcher]                         = GF(MainApp.imageMatcher)
  implicit val gdSystemUtilsImplicit: GF[GDSystemUtils]                       = GF(MainApp.gdSystemUtils)
  implicit val webappListenerImplicit: GF[ActorRef[WebAppListener.GoHomeKey]] = GF(MainApp.webappListener)
  implicit def delayBuffUIImplicit: GF[DelayBuffUI]                           = GF(MainApp.delayBuffUI)
}

class GlobalVarsFetch[T, Poly](val value: T)
