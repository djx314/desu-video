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

  def apply(): Behavior[ActionStatus] = Behaviors.setup(s => new ActionQueue(s))
}

import ActionQueue._
class ActionQueue(context: ActorContext[ActionStatus]) extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  private var currentAction: Future[Any] = Future.successful(())

  private def appendAction(n: => Future[Any]): Unit = currentAction = currentAction.transformWith(_ => n)

  private val delayCall: Callable[Future[Any]] = {
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
        def action = SystemRobot.keyType(keyCode)
        appendAction(action)
      case KeyPressDown(keyCode) =>
        def action = SystemRobot.keyPress(keyCode)
        appendAction(action)
      case KeyPressUp(keyCode) =>
        def action = SystemRobot.keyRelease(keyCode)
        appendAction(action)
      case MouseDown       => appendAction(SystemRobot.mouseDown)
      case MouseUp         => appendAction(SystemRobot.mouseUp)
      case MouseClick      => appendAction(SystemRobot.mouseClick)
      case MouseRightClick => appendAction(SystemRobot.mouseRightClick)
      case MouseMove(x, y) =>
        def action = SystemRobot.mouseMove(x = x, y = y)
        appendAction(action)
      case ActionInputDelay(millions) => appendAction(delayMillions(millions))
      case s: ReplyToImpl =>
        def action = Future(s.actor ! s.message)
        appendAction(action)
    }
    Behaviors.same
  }
}
