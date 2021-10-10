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
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.util.{Failure, Success}

object WebAppListener {
  trait GoHomeKey
  case object StartGoHomeKeyListener               extends GoHomeKey
  case class StartActionComplete(isReady: Boolean) extends GoHomeKey
  case object PressGoHomeKeyBoard                  extends GoHomeKey
  case object PressEnableBuffBoard                 extends GoHomeKey
  case object RoundAction                          extends GoHomeKey
  case object ReadyToListen                        extends GoHomeKey
  case object StopWebSystem                        extends GoHomeKey

  def apply(binding: Future[ServerBinding]): Behavior[GoHomeKey] = Behaviors.setup(s => new WebAppListener(s, binding))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey], binding: Future[ServerBinding]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey] = context.spawnAnonymous(GoHomeKeyListener())
  val enableBuffAction: ActorRef[EnableBuffAction.GoHomeKey]    = context.spawnAnonymous(EnableBuffAction())
  val 重生之语: ActorRef[SkillsRoundAction1.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyEvent.VK_6, delay = 15000))
  val 蓝药: ActorRef[SkillsRoundAction1.GoHomeKey]   = context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyEvent.VK_TAB, delay = 27000))

  var isReady: Boolean = false

  def stopWebSystem = {
    def stopHotKey() = Future(GDHotKeyListener.stopListen)(blockExecutionContext)
    val cloneAction = for {
      _               <- stopHotKey()
      bindingInstance <- binding
      _               <- bindingInstance.unbind()
    } yield {}
    cloneAction.onComplete(_ => system.terminate())
  }

  self ! StartGoHomeKeyListener

  private def delayMillions[T](million: Long, callable: Callable[Future[T]]): Future[T] = Patterns.after(
    Duration(million, MILLISECONDS),
    system.classicSystem.scheduler,
    implicitly[ExecutionContext],
    callable
  )

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case StartGoHomeKeyListener =>
        context.pipeToSelf(Future(GDHotKeyListener.startListen(self))(blockExecutionContext)) {
          case Success(value) => StartActionComplete(true)
          case Failure(err)   => StartActionComplete(false)
        }
      case StartActionComplete(r) => isReady = r
      case PressGoHomeKeyBoard    => if (isReady) pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
      case PressEnableBuffBoard   => if (isReady) enableBuffAction ! EnableBuffAction.PressGoHomeKeyBoard
      case RoundAction =>
        if (isReady) {
          重生之语 ! SkillsRoundAction1.PressGoHomeKeyBoard
          delayMillions(1000, () => Future(蓝药 ! SkillsRoundAction1.PressGoHomeKeyBoard))
        }
      case StopWebSystem => stopWebSystem
    }
    Behaviors.same
  }
}
