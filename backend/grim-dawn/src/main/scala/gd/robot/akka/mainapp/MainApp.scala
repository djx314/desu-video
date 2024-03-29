package gd.robot.akka.mainapp

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import distage.{Activation, DIKey, Functoid, Injector, ModuleDef, Roots}
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.{GDHotKeyListener, WebAppListener}
import gd.robot.akka.ui.{DelayBuff, DelayBuffUI}
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher, ImageMatcherEnv, ImageUtils}
import izumi.distage.model.Locator
import zio._

object GDModule extends ModuleDef {
  make[ImageMatcher]
  make[GDSystemUtils]
  make[ImageUtils]
  make[AppConfig]
  make[ImageMatcherEnv].from {
    appConfig: AppConfig =>
      appConfig.imgMatch
  }
  make[ActorSystem[WebAppListener.GoHomeKey]].fromResource {
    val actorManaged =
      ZManaged.make(ZIO.effect(ActorSystem(WebAppListener(), "gd-system")))(system => ZIO.effect(system.terminate()).option)
    for {
      actor <- actorManaged
      // _     <- GDHotKeyListener.startListen(actor)
    } yield actor
  }
  make[() => DelayBuff]
  make[DelayBuffUI]
}

object GDApp {
  private val injector = Injector[RIO[blocking.Blocking with console.Console, *]]()
  private val plan = injector.plan(
    GDModule,
    Activation.empty,
    Roots(
      DIKey[ImageMatcher],
      DIKey[GDSystemUtils],
      DIKey[ActorSystem[WebAppListener.GoHomeKey]],
      DIKey[DelayBuffUI]
    )
  )
  private val preResource                      = injector.produce(plan)
  private val Reservation(require, preRelease) = Runtime.default.unsafeRun(preResource.toZIO.reserve)
  val resource: Locator                        = Runtime.default.unsafeRun(require)
  val release                                  = preRelease
}
