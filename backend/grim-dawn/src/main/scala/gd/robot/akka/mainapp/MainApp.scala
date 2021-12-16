package gd.robot.akka.mainapp

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import distage.{Activation, DIKey, Functoid, Injector, ModuleDef, Roots}
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.{GDHotKeyListener, WebAppListener}
import gd.robot.akka.ui.{DelayBuff, DelayBuffUI}
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher, ImageMatcherEnv, ImageUtils}
import izumi.distage.model.Locator
import zio.{RIO, _}
import zio.console.Console
import izumi.functional.bio.{Error2, F, Primitives2}

/*object MainApp {

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
}*/

class GlobalVarsFetch[T, Poly](val value: T)

object GDModule extends ModuleDef {
  val actorSystemZManaged = ZManaged.makeEffect(ActorSystem(Behaviors.empty, "gd-system"))(system => system.terminate())
  make[ActorSystem[Nothing]].fromResource(actorSystemZManaged)
  make[ImageMatcher]
  make[GDSystemUtils]
  make[ImageUtils]
  make[AppConfig]
  make[ImageMatcherEnv].from { appConfig: AppConfig =>
    appConfig.imgMatch
  }
  make[GDHotKeyListener.type].fromResource { actor: ActorRef[WebAppListener.GoHomeKey] =>
    val pre = blocking.effectBlocking(GDHotKeyListener.startListen(actor))
    ZManaged.fromAutoCloseable(for (_ <- pre) yield GDHotKeyListener)
  }
  make[ActorRef[WebAppListener.GoHomeKey]].from { system: ActorSystem[Nothing] =>
    system.systemActorOf(WebAppListener(), "web-app-listener")
  }
  make[() => DelayBuff]
  make[DelayBuffUI]
}

object GDApp {
  val injector = Injector[Task[*]]()
  val plan = injector.plan(
    GDModule,
    Activation.empty,
    Roots(DIKey[ImageMatcher], DIKey[GDSystemUtils], DIKey[ActorRef[WebAppListener.GoHomeKey]], DIKey[DelayBuffUI])
  )
  val preResource = injector.produce(plan)
  // val managed =
  // resource.map(s => (s.get[ImageMatcher], s.get[GDSystemUtils], s.get[ActorRef[WebAppListener.GoHomeKey]], s.get[DelayBuffUI]))
  val Reservation(require, release) = Runtime.default.unsafeRun(preResource.toZIO.reserve)
  val resource: Locator             = Runtime.default.unsafeRun(require)
}

/*object GlobalVars {
  import GlobalVarsInject._
  def apply[T](implicit f: GF[T]): T = f.value
}

object GlobalVarsInject {
  type GF[T] = GlobalVarsFetch[T, GlobalVarsInject.type]
  def GF[T](t: T): GF[T] = new GlobalVarsFetch(t)

  implicit val imageMatcherImplicit: GF[ImageMatcher]                         = GF(GDApp.imageMatcher)
  implicit val gdSystemUtilsImplicit: GF[GDSystemUtils]                       = GF(GDApp.gdSystemUtils)
  implicit val webappListenerImplicit: GF[ActorRef[WebAppListener.GoHomeKey]] = GF(GDApp.webappListener)
  implicit def delayBuffUIImplicit: GF[DelayBuffUI]                           = GF(GDApp.delayBuffUI)
}*/
