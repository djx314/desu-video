package gd.robot.akka.utils

import akka.actor.typed.{ActorRef, ActorSystem, DispatcherSelector}
import akka.util.Timeout
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.systemactor.WaitForGDFocus
import scalafx.application.Platform
import scalafx.geometry.Rectangle2D
import scalafx.stage.Screen

import java.awt.Toolkit
import scala.concurrent.{Future, Promise}

class GDSystemUtils(system: ActorSystem[Nothing], imageUtils: ImageUtils) {
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.scheduler

  val 开始菜单图标Byte     = ImageUtils.getBytesFromClasspath("/窗口定位/开始菜单栏图标.png")
  val 恐怖黎明拾落选择按钮Byte = ImageUtils.getBytesFromClasspath("/窗口定位/恐怖黎明拾落选择按钮.png")

  val waitForGDFocus: ActorRef[WaitForGDFocus.ActionStatus] = system.systemActorOf(WaitForGDFocus(), "wait-for-gd-focus-actor")
  waitForGDFocus ! WaitForGDFocus.CheckGDFocus(false)

  def isNowOnFocus: Future[Boolean] = {
    def picF(width: Int, height: Int) = imageUtils.screenshotF(0, height - 80, width, height)

    for {
      size <- SystemRobot.screenSize
      pic  <- picF(size.width.toInt, size.height.toInt)
      r    <- imageUtils.matchImg(开始菜单图标Byte, pic)
    } yield r.isDefined

  }

  import akka.actor.typed.scaladsl.AskPattern._
  import scala.concurrent.duration._
  implicit val timeout = Timeout(1000.hours)

  def waitForFocus[T](f: => Future[T]): Future[T] = {
    val future = waitForGDFocus ? ((actor: ActorRef[Boolean]) => WaitForGDFocus.InputPromise(actor))
    future.flatMap((_: Boolean) => f)
  }

  def waitForFocus: Future[Unit] = {
    val future = waitForGDFocus ? ((actor: ActorRef[Boolean]) => WaitForGDFocus.InputPromise(actor))
    future.map((_: Boolean) => ())
  }

}
