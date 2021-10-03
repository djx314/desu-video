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
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey] = context.spawnAnonymous(GoHomeKeyListener())

  val gdHotKeyListener       = new GDHotKeyListener(context.self)
  var readyToListen: Boolean = false

  def stopWebSystem = {
    def stopHotKey() = Future(GDHotKeyListener.stopListen)(blockExecutionContext)
    val cloneAction = for {
      _               <- stopHotKey()
      bindingInstance <- binding
      _               <- bindingInstance.unbind()
    } yield {}
    cloneAction.onComplete(_ => system.terminate())
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case StartGoHomeKeyListener =>
        // gdHotKeyListener = new GDHotKeyListener(context.self)
        Future(gdHotKeyListener.startListen)(blockExecutionContext)
      case PressGoHomeKeyBoard => pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
      case ReadyToListen       => readyToListen = true
      case StopWebSystem       => stopWebSystem
    }
    Behaviors.same
  }
}
