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
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  var currentAction: Future[Any] = Future.successful(())

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case ActionInputCommon(action) =>
        currentAction = currentAction.flatMap(_ => action())
      case ActionInputDelay(millions) =>
        currentAction = currentAction.flatMap(_ =>
          Patterns.after(
            Duration(millions, MILLISECONDS),
            context.system.classicSystem.scheduler,
            implicitly[ExecutionContext],
            () => Future.successful(())
          )
        )
    }
    Behaviors.same
  }
}
