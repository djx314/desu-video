package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.actor.{Scheduler => CScheduler}
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.SystemRobot
import javafx.scene.input.KeyCode

import java.util.concurrent.Callable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}

object ActionQueue {
  trait ActionStatus
  case class KeyType(keyCode: KeyCode)        extends ActionStatus
  case class KeyPressDown(keyCode: KeyCode)   extends ActionStatus
  case class KeyPressUp(keyCode: KeyCode)     extends ActionStatus
  case object MouseDown                       extends ActionStatus
  case object MouseUp                         extends ActionStatus
  case object MouseClick                      extends ActionStatus
  case object MouseRightClick                 extends ActionStatus
  case class MouseMove(x: Int, y: Int)        extends ActionStatus
  case class ActionInputDelay(millions: Long) extends ActionStatus
  trait ReplyToImpl extends ActionStatus {
    type Message
    val actor: ActorRef[Message]
    val message: Message
  }
  case class ReplyTo[T](override val actor: ActorRef[T], override val message: T) extends ReplyToImpl {
    override type Message = T
  }

  def init(): Behavior[ActionStatus]                     = Behaviors.setup(s => new ActionQueue(s, Future.successful(())))
  def apply(future: Future[Any]): Behavior[ActionStatus] = Behaviors.setup(s => new ActionQueue(s, future))
}

import ActionQueue._
class ActionQueue(context: ActorContext[ActionStatus], action: Future[Any]) extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.classicSystem.scheduler

  private def delayCall: Callable[Future[Any]] = {
    val f = Future.successful(())
    () => f
  }
  private def delayMillions[T](million: Long): Future[Any] = Patterns.after(
    Duration(million, MILLISECONDS),
    implicitly[CScheduler],
    implicitly[ExecutionContext],
    delayCall
  )

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case KeyType(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyType(keyCode))
        ActionQueue(newAction)
      case KeyPressDown(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyPress(keyCode))
        ActionQueue(newAction)
      case KeyPressUp(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyRelease(keyCode))
        ActionQueue(newAction)
      case MouseDown =>
        val newAction = action.transformWith(_ => SystemRobot.mouseDown)
        ActionQueue(newAction)
      case MouseUp =>
        val newAction = action.transformWith(_ => SystemRobot.mouseUp)
        ActionQueue(newAction)
      case MouseClick =>
        val newAction = action.transformWith(_ => SystemRobot.mouseClick)
        ActionQueue(newAction)
      case MouseRightClick =>
        val newAction = action.transformWith(_ => SystemRobot.mouseRightClick)
        ActionQueue(newAction)
      case MouseMove(x, y) =>
        val newAction = action.transformWith(_ => SystemRobot.mouseMove(x = x, y = y))
        ActionQueue(newAction)
      case ActionInputDelay(millions) =>
        val newAction = action.transformWith(_ => delayMillions(millions))
        ActionQueue(newAction)
      case s: ReplyToImpl =>
        val newAction = action.transformWith(_ => Future(s.actor ! s.message))
        ActionQueue(newAction)
    }
  }
}
