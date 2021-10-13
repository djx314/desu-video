package gd.robot.akka.utils

import akka.actor.typed.{ActorRef, ActorSystem, DispatcherSelector}
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.systemactor.WaitForGDFocus

import java.awt.Toolkit
import scala.concurrent.{Future, Promise}

class GDSystemUtils(system: ActorSystem[Nothing], imageUtils: ImageUtils) {

  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  val 开始菜单图标Byte     = ImageUtils.getBytesFromClasspath("/窗口定位/开始菜单栏图标.png")
  val 恐怖黎明拾落选择按钮Byte = ImageUtils.getBytesFromClasspath("/窗口定位/恐怖黎明拾落选择按钮.png")

  val waitForGDFocus: ActorRef[WaitForGDFocus.ActionStatus] = system.systemActorOf(WaitForGDFocus(), "wait-for-gd-focus-actor")

  def isNowOnFocus: Future[Boolean] = {
    val size   = Toolkit.getDefaultToolkit().getScreenSize()
    val width  = size.width
    val height = size.height
    val picF   = imageUtils.screenshotF(0, height - 80, width, height)
    for {
      pic <- picF
      r   <- imageUtils.matchImg(开始菜单图标Byte, pic)
    } yield r.isDefined
  }

  def waitForFocus[T](f: => Future[T]): Future[T] = {
    val promise = Promise[Boolean]()
    waitForGDFocus ! WaitForGDFocus.InputPromise(promise)
    val future = promise.future
    future.flatMap((_: Boolean) => f)
  }

  def waitForFocus: Future[Unit] = {
    val promise = Promise[Boolean]()
    waitForGDFocus ! WaitForGDFocus.InputPromise(promise)
    val future = promise.future
    future.map((_: Boolean) => ())
  }

}
