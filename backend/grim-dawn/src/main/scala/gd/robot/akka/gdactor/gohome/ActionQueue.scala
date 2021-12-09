package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
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

  def apply(): Behavior[ActionStatus] = Behaviors.setup(s => new ActionQueue(s, Future.successful(())))
}

import ActionQueue._
class ActionQueue(context: ActorContext[ActionStatus], action: Future[Any]) extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  private def delayCall: Callable[Future[Any]] = {
    val f = Future.successful(())
    () => f
  }
  private def delayMillions[T](million: Long): Future[Any] = Patterns.after(
    Duration(million, MILLISECONDS),
    system.classicSystem.scheduler,
    implicitly[ExecutionContext],
    delayCall
  )

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case KeyType(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyType(keyCode))
        new ActionQueue(context, newAction)
      case KeyPressDown(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyPress(keyCode))
        new ActionQueue(context, newAction)
      case KeyPressUp(keyCode) =>
        val newAction = action.transformWith(_ => SystemRobot.keyRelease(keyCode))
        new ActionQueue(context, newAction)
      case MouseDown =>
        val newAction = action.transformWith(_ => SystemRobot.mouseDown)
        new ActionQueue(context, newAction)
      case MouseUp =>
        val newAction = action.transformWith(_ => SystemRobot.mouseUp)
        new ActionQueue(context, newAction)
      case MouseClick =>
        val newAction = action.transformWith(_ => SystemRobot.mouseClick)
        new ActionQueue(context, newAction)
      case MouseRightClick =>
        val newAction = action.transformWith(_ => SystemRobot.mouseRightClick)
        new ActionQueue(context, newAction)
      case MouseMove(x, y) =>
        val newAction = action.transformWith(_ => SystemRobot.mouseMove(x = x, y = y))
        new ActionQueue(context, newAction)
      case ActionInputDelay(millions) =>
        val newAction = action.transformWith(_ => delayMillions(millions))
        new ActionQueue(context, newAction)
      case s: ReplyToImpl =>
        val newAction = action.transformWith(_ => Future(s.actor ! s.message))
        new ActionQueue(context, newAction)
    }
  }
}
