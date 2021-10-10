package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig

import java.util.concurrent.Callable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}

object ActionQueue {
  trait ActionStatus
  case class ActionInputCommon(action: () => Future[Any]) extends ActionStatus
  case class ActionInputDelay(millions: Long)             extends ActionStatus

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
      case ActionInputCommon(action)  => appendAction(action())
      case ActionInputDelay(millions) => appendAction(delayMillions(millions))
    }
    Behaviors.same
  }
}