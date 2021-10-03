package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.http.scaladsl.Http.ServerBinding
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import org.w3c.dom.events.MouseEvent

import java.awt.Robot
import java.awt.event.{InputEvent, KeyEvent}
import java.util.concurrent.Callable
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.util.{Failure, Success}

object WebAppListener {
  trait GoHomeKey
  case object StartGoHomeKeyListener extends GoHomeKey
  case object PressGoHomeKeyBoard    extends GoHomeKey
  case object ReadyToListen          extends GoHomeKey
  case object StopWebSystem          extends GoHomeKey

  def apply(binding: Future[ServerBinding]): Behavior[GoHomeKey] = Behaviors.setup(s => new WebAppListener(s, binding))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey], binding: Future[ServerBinding]) extends AbstractBehavior[GoHomeKey](context) {
  val blockExecutionContext     = context.system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = context.system.dispatchers.lookup(AppConfig.gdSelector)

  var gdHotKeyListener: GDHotKeyListener = null
  var readyToListen: Boolean             = false


  def stopWebSystem = {
    val system = context.system
    val stopHotKey = Future(gdHotKeyListener.stopListen)(blockExecutionContext)
   val cloneAction = for {
    _ <- stopHotKey
    binding1 <- binding
    _ <- binding1.unbind()} yield {}
    cloneAction.onComplete(_ => system.terminate())
  }



  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case StartGoHomeKeyListener =>
        gdHotKeyListener = new GDHotKeyListener(context.self)
        Future(gdHotKeyListener.startListen)(blockExecutionContext)
      case PressGoHomeKeyBoard => mouseRobot
      case ReadyToListen       => readyToListen = true
      case StopWebSystem       => stopWebSystem
    }
    Behaviors.same
  }
}
