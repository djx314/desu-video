package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig

import java.util
import scala.concurrent.Future

object ActionQueue {
  trait ActionStatus
  case class ActionInputCommon(action: () => Future[Any]) extends ActionStatus
  case class ActionInputDelay(millions: Long)             extends ActionStatus
  case class ActionOutPut(action: () => Future[Any])      extends ActionStatus

  def apply(): Behavior[ActionStatus] = Behaviors.setup(s => new ActionQueue(s))
}

import ActionQueue._
class ActionQueue(context: ActorContext[ActionStatus]) extends AbstractBehavior[ActionStatus](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  var currentAction: util.Queue[() => Future[Any]] = new util.LinkedList

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case ActionInputCommon(action) =>
        if (currentPress == false) {
          currentPress = true
          context.pipeToSelf(mouseRobot) { _ =>
            PressFinish
          }
        }
      case ActionInputDelay(millions) => currentPress = false
      case ActionOutPut(action)       =>
    }
    Behaviors.same
  }
}
