package gd.robot.akka.gdactor.gohome.systemactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars

import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, Future, Promise}

object WaitForGDFocus {
  trait ActionStatus
  case class InputPromise(promise: ActorRef[Boolean]) extends ActionStatus
  case class CheckGDFocus(focus: Boolean)             extends ActionStatus

  def apply(): Behavior[ActionStatus]                               = Behaviors.setup(s => new WaitForGDFocus(s, List.empty))
  def apply2(list: List[ActorRef[Boolean]]): Behavior[ActionStatus] = Behaviors.setup(s => new WaitForGDFocus(s, list))
}

import WaitForGDFocus._
class WaitForGDFocus(context: ActorContext[ActionStatus], promiseList: List[ActorRef[Boolean]])
    extends AbstractBehavior[ActionStatus](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val gdSystemUtils             = GlobalVars.gdSystemUtils
  private val self                      = context.self

  private def delayCheck[T](million: Long): Future[CheckGDFocus] = Patterns.after(
    Duration(million, MILLISECONDS),
    system.classicSystem.scheduler,
    implicitly[ExecutionContext],
    () => for (focus <- gdSystemUtils.isNowOnFocus) yield CheckGDFocus(focus)
  )

  override def onMessage(msg: ActionStatus): Behavior[ActionStatus] = {
    msg match {
      case InputPromise(keyCode) =>
        WaitForGDFocus.apply2(keyCode :: promiseList)
      case CheckGDFocus(focus) =>
        context.pipeToSelf(delayCheck(500))(_.getOrElse(CheckGDFocus(false)))
        if (focus) {
          for (p <- promiseList) yield p ! focus
          WaitForGDFocus()
        } else Behaviors.same
    }
  }
}
